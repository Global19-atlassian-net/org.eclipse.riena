/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.riena.core.util.ListenerList;
import org.eclipse.riena.ui.common.ISortableByColumn;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.databinding.IUnboundPropertyObservable;
import org.eclipse.riena.ui.ridgets.databinding.UnboundPropertyWritableList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Ridget for SWT {@link Table} widgets.
 */
public class TableRidget extends AbstractSelectableIndexedRidget implements ITableRidget {

	private static final Listener eraseListener = new EraseListener();

	private final SelectionListener selectionTypeEnforcer;
	private final MouseListener doubleClickForwarder;
	private final ColumnSortListener sortListener;
	private ListenerList<IActionListener> doubleClickListeners;

	private DataBindingContext dbc;
	/*
	 * Binds the viewer's multiple selection to the multiple selection
	 * observable. This binding hsa to be disposed when the ridget is set to
	 * output-only, to avoid updating the model. It has to be recreated when the
	 * ridget is set to not-output-only.
	 */
	private Binding viewerMSB;

	private TableViewer viewer;
	private String[] columnHeaders;
	private IObservableList rowObservables;
	private Class<?> rowBeanClass;
	private String[] renderingMethods;

	private boolean isSortedAscending;
	private int sortedColumn;
	private final Map<Integer, Boolean> sortableColumnsMap;
	private final Map<Integer, Comparator<Object>> comparatorMap;
	private final Map<Integer, Object> labelProviderMap;

	private boolean moveableColumns;

	public TableRidget() {
		selectionTypeEnforcer = new SelectionTypeEnforcer();
		doubleClickForwarder = new DoubleClickForwarder();
		sortListener = new ColumnSortListener();
		isSortedAscending = true;
		sortedColumn = -1;
		sortableColumnsMap = new HashMap<Integer, Boolean>();
		comparatorMap = new HashMap<Integer, Comparator<Object>>();
		labelProviderMap = new HashMap<Integer, Object>();
		addPropertyChangeListener(IMarkableRidget.PROPERTY_ENABLED, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				applyEraseListener();
			}
		});
		addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (isOutputOnly()) {
					disposeMultipleSelectionBinding();
				} else {
					createMultipleSelectionBinding();
				}
			}
		});
	}

	@Override
	protected void checkUIControl(Object uiControl) {
		AbstractSWTRidget.assertType(uiControl, Table.class);
	}

	@Override
	protected void bindUIControl() {
		final Table control = getUIControl();
		if (control != null && rowObservables != null) {
			checkColumns(control);
			viewer = new TableViewer(control);
			final ObservableListContentProvider viewerCP = new ObservableListContentProvider();
			IObservableMap[] attrMap = BeansObservables.observeMaps(viewerCP.getKnownElements(), rowBeanClass,
					renderingMethods);
			Object[] labelProviders = getLabelProviders(attrMap.length);
			viewer.setLabelProvider(new TableRidgetLabelProvider(attrMap, labelProviders));
			viewer.setContentProvider(viewerCP);

			applyColumnsMoveable(control);
			applyTableColumnHeaders(control);
			applyComparator();
			applyEraseListener();

			viewer.setInput(rowObservables);

			dbc = new DataBindingContext();
			// viewer to single selection binding
			IObservableValue viewerSelection = ViewersObservables.observeSingleSelection(viewer);
			dbc.bindValue(viewerSelection, getSingleSelectionObservable(), new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_UPDATE).setAfterGetValidator(new OutputAwareValidator(this)),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			// viewer to to multi selection binding
			viewerMSB = null;
			if (!isOutputOnly()) {
				createMultipleSelectionBinding();
			}

			for (TableColumn column : control.getColumns()) {
				column.addSelectionListener(sortListener);
			}
			control.addSelectionListener(selectionTypeEnforcer);
			control.addMouseListener(doubleClickForwarder);
		}
	}

	@Override
	protected void unbindUIControl() {
		if (dbc != null) {
			disposeMultipleSelectionBinding();
			dbc.dispose();
			dbc = null;
		}
		Table control = getUIControl();
		if (control != null) {
			for (TableColumn column : control.getColumns()) {
				column.removeSelectionListener(sortListener);
			}
			control.removeSelectionListener(selectionTypeEnforcer);
			control.removeMouseListener(doubleClickForwarder);
			control.removeListener(SWT.EraseItem, eraseListener);
		}
		viewer = null;
	}

	@Override
	protected java.util.List<?> getRowObservables() {
		return rowObservables;
	}

	@Override
	public Table getUIControl() {
		return (Table) super.getUIControl();
	}

	public void addDoubleClickListener(IActionListener listener) {
		Assert.isNotNull(listener, "listener is null"); //$NON-NLS-1$
		if (doubleClickListeners == null) {
			doubleClickListeners = new ListenerList<IActionListener>(IActionListener.class);
		}
		doubleClickListeners.add(listener);
	}

	public void bindToModel(IObservableList rowBeansObservable, Class<? extends Object> rowBeanClass,
			String[] columnPropertyNames, String[] columnHeaders) {
		if (columnHeaders != null) {
			String msg = "Mismatch between number of columnPropertyNames and columnHeaders"; //$NON-NLS-1$
			Assert.isLegal(columnPropertyNames.length == columnHeaders.length, msg);
		}
		unbindUIControl();

		this.rowBeanClass = rowBeanClass;
		rowObservables = rowBeansObservable;
		renderingMethods = new String[columnPropertyNames.length];
		System.arraycopy(columnPropertyNames, 0, renderingMethods, 0, renderingMethods.length);

		if (columnHeaders != null) {
			this.columnHeaders = new String[columnHeaders.length];
			System.arraycopy(columnHeaders, 0, this.columnHeaders, 0, this.columnHeaders.length);
		} else {
			this.columnHeaders = null;
		}

		bindUIControl();
	}

	public void bindToModel(Object listBean, String listPropertyName, Class<? extends Object> rowBeanClass,
			String[] columnPropertyNames, String[] columnHeaders) {
		IObservableList listObservableValue = new UnboundPropertyWritableList(listBean, listPropertyName);
		bindToModel(listObservableValue, rowBeanClass, columnPropertyNames, columnHeaders);
	}

	@Override
	public void updateFromModel() {
		super.updateFromModel();
		if (viewer != null) {
			// prevent flicker during update
			viewer.getControl().setRedraw(false);
			StructuredSelection currentSelection = new StructuredSelection(getSelection());
			try {
				if (rowObservables instanceof IUnboundPropertyObservable) {
					((UnboundPropertyWritableList) rowObservables).updateFromBean();
				}
				viewer.refresh(true);
			} finally {
				viewer.setSelection(currentSelection);
				viewer.getControl().setRedraw(true);
			}
		}
	}

	public IObservableList getObservableList() {
		return rowObservables;
	}

	public void removeDoubleClickListener(IActionListener listener) {
		if (doubleClickListeners != null) {
			doubleClickListeners.remove(listener);
		}
	}

	public void setComparator(int columnIndex, Comparator<Object> compi) {
		checkColumnRange(columnIndex);
		Integer key = Integer.valueOf(columnIndex);
		if (compi != null) {
			comparatorMap.put(key, compi);
		} else {
			comparatorMap.remove(key);
		}
		if (columnIndex == sortedColumn) {
			applyComparator();
		}
	}

	public int getSortedColumn() {
		int result = -1;
		Table table = getUIControl();
		if (table != null) {
			TableColumn column = table.getSortColumn();
			if (column != null) {
				result = table.indexOf(column);
			}
		}
		return result;
	}

	public boolean isColumnSortable(int columnIndex) {
		checkColumnRange(columnIndex);
		boolean result = false;
		Integer key = Integer.valueOf(columnIndex);
		Boolean sortable = sortableColumnsMap.get(columnIndex);
		if (sortable == null || Boolean.TRUE.equals(sortable)) {
			result = comparatorMap.get(key) != null;
		}
		return result;
	}

	public boolean isSortedAscending() {
		boolean result = false;
		Table table = getUIControl();
		if (table != null) {
			int sortDirection = table.getSortDirection();
			result = (sortDirection == SWT.UP);
		}
		return result;
	}

	public void setColumnSortable(int columnIndex, boolean sortable) {
		checkColumnRange(columnIndex);
		Integer key = Integer.valueOf(columnIndex);
		Boolean newValue = Boolean.valueOf(sortable);
		Boolean oldValue = sortableColumnsMap.put(key, newValue);
		if (oldValue == null) {
			oldValue = Boolean.TRUE;
		}
		if (!newValue.equals(oldValue)) {
			firePropertyChange(ISortableByColumn.PROPERTY_COLUMN_SORTABILITY, null, columnIndex);
		}
	}

	public void setSortedAscending(boolean ascending) {
		if (isSortedAscending != ascending) {
			boolean oldSortedAscending = isSortedAscending;
			isSortedAscending = ascending;
			applyComparator();
			firePropertyChange(ISortableByColumn.PROPERTY_SORT_ASCENDING, oldSortedAscending, isSortedAscending);
		}
	}

	public void setSortedColumn(int columnIndex) {
		if (columnIndex != -1) {
			checkColumnRange(columnIndex);
		}
		if (sortedColumn != columnIndex) {
			int oldSortedColumn = sortedColumn;
			sortedColumn = columnIndex;
			applyComparator();
			firePropertyChange(ISortableByColumn.PROPERTY_SORTED_COLUMN, oldSortedColumn, sortedColumn);
		}
	}

	@Override
	public int getSelectionIndex() {
		Table control = getUIControl();
		return control == null ? -1 : control.getSelectionIndex();
	}

	@Override
	public int[] getSelectionIndices() {
		Table control = getUIControl();
		return control == null ? new int[0] : control.getSelectionIndices();
	}

	@Override
	public int indexOfOption(Object option) {
		Table control = getUIControl();
		if (control != null) {
			// implies viewer != null
			int optionCount = control.getItemCount();
			for (int i = 0; i < optionCount; i++) {
				if (viewer.getElementAt(i).equals(option)) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean hasMoveableColumns() {
		return moveableColumns;
	}

	public void setMoveableColumns(boolean moveableColumns) {
		if (this.moveableColumns != moveableColumns) {
			this.moveableColumns = moveableColumns;
			Table control = getUIControl();
			if (control != null) {
				applyColumnsMoveable(control);
			}
		}
	}

	/**
	 * Always returns true because mandatory markers do not make sense for this
	 * ridget.
	 */
	@Override
	public boolean isDisableMandatoryMarker() {
		return true;
	}

	/**
	 * This method is provisional. Do not use yet.
	 */
	// TODO [ev] discuss this
	public void setLabelProvider(int columnIndex, Object labelProvider) {
		checkColumnRange(columnIndex);
		Integer key = Integer.valueOf(columnIndex);
		labelProviderMap.put(key, labelProvider);
	}

	// helping methods
	// ////////////////

	private void applyColumnsMoveable(Table control) {
		for (TableColumn column : control.getColumns()) {
			column.setMoveable(moveableColumns);
		}
	}

	private void applyComparator() {
		if (viewer != null) {
			Table table = viewer.getTable();
			Comparator<Object> compi = null;
			if (sortedColumn != -1) {
				Integer key = Integer.valueOf(sortedColumn);
				compi = comparatorMap.get(key);
			}
			if (compi != null) {
				TableColumn column = table.getColumn(sortedColumn);
				table.setSortColumn(column);
				int direction = isSortedAscending ? SWT.UP : SWT.DOWN;
				table.setSortDirection(direction);
				SortableComparator sortableComparator = new SortableComparator(this, compi);
				viewer.setComparator(new TableComparator(sortableComparator));
			} else {
				viewer.setComparator(null);
				table.setSortColumn(null);
				table.setSortDirection(SWT.NONE);
			}
		}
	}

	private void applyEraseListener() {
		if (viewer != null) {
			Control control = viewer.getControl();
			control.removeListener(SWT.EraseItem, eraseListener);
			if (!isEnabled() && MarkerSupport.HIDE_DISABLED_RIDGET_CONTENT) {
				control.addListener(SWT.EraseItem, eraseListener);
			}
		}
	}

	private void applyTableColumnHeaders(Table control) {
		boolean headersVisible = columnHeaders != null;
		control.setHeaderVisible(headersVisible);
		if (headersVisible) {
			TableColumn[] columns = control.getColumns();
			for (int i = 0; i < columns.length; i++) {
				String columnHeader = ""; //$NON-NLS-1$
				if (i < columnHeaders.length && columnHeaders[i] != null) {
					columnHeader = columnHeaders[i];
				}
				columns[i].setText(columnHeader);
			}
		}
	}

	private void checkColumns(Table control) {
		int columnCount = control.getColumnCount() == 0 ? 1 : control.getColumnCount();
		String message = String.format("Table has %d columns, expected: %d", columnCount, renderingMethods.length); //$NON-NLS-1$
		Assert.isLegal(columnCount == renderingMethods.length, message);
	}

	private void checkColumnRange(int columnIndex) {
		Table table = getUIControl(); // table may be null if unbound
		int range = table.getColumnCount();
		String msg = "columnIndex out of range (0 - " + range + " ): " + columnIndex; //$NON-NLS-1$ //$NON-NLS-2$
		Assert.isLegal(-1 < columnIndex, msg);
		Assert.isLegal(columnIndex < range, msg);
	}

	private void createMultipleSelectionBinding() {
		if (viewerMSB == null && dbc != null && viewer != null) {
			StructuredSelection currentSelection = new StructuredSelection(getSelection());
			IViewerObservableList viewerSelections = ViewersObservables.observeMultiSelection(viewer);
			viewerMSB = dbc.bindList(viewerSelections, getMultiSelectionObservable(), new UpdateListStrategy(
					UpdateListStrategy.POLICY_UPDATE), new UpdateListStrategy(UpdateListStrategy.POLICY_UPDATE));
			viewer.setSelection(currentSelection);
		}
	}

	private void disposeMultipleSelectionBinding() {
		if (viewerMSB != null) { // implies dbc != null
			viewerMSB.dispose();
			dbc.removeBinding(viewerMSB);
			viewerMSB = null;
		}
	}

	private Object[] getLabelProviders(int numColumns) {
		Assert.isLegal(numColumns >= 0);
		Object[] result = new Object[numColumns];
		for (int i = 0; i < numColumns; i++) {
			Object labelProvider = labelProviderMap.get(Integer.valueOf(i));
			if (labelProvider != null) {
				result[i] = labelProvider;
			}
		}
		return result;
	}

	// helping classes
	// ////////////////

	/**
	 * Enforces selection in the control:
	 * <ul>
	 * <li>disallows selection changes when the ridget is "output only"</li>
	 * <li>disallows multiple selection is the selection type of the ridget is
	 * {@link ISelectableRidget.SelectionType#SINGLE}</li>
	 * </ul>
	 */
	private final class SelectionTypeEnforcer extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (isOutputOnly()) {
				// undo user selection when "output only"
				viewer.setSelection(new StructuredSelection(getSelection()));
			} else if (SelectionType.SINGLE.equals(getSelectionType())) {
				Table control = (Table) e.widget;
				if (control.getSelectionCount() > 1) {
					// ignore this event
					e.doit = false;
					// set selection to most recent item
					control.setSelection(control.getSelectionIndex());
					// fire event
					Event event = new Event();
					event.type = SWT.Selection;
					event.doit = true;
					control.notifyListeners(SWT.Selection, event);
				}
			}
		}
	}

	/**
	 * Notifies doubleClickListeners when the bound widget is double clicked.
	 */
	private final class DoubleClickForwarder extends MouseAdapter {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			if (doubleClickListeners != null) {
				for (IActionListener listener : doubleClickListeners.getListeners()) {
					listener.callback();
				}
			}
		}
	}

	/**
	 * Erase listener to paint all cells empty when this ridget is disabled.
	 * <p>
	 * Implementation note: this works by registering this class an an
	 * EraseEListener and indicating we will be repsonsible from drawing the
	 * cells content. We do not register a PaintListener, meaning that we do NOT
	 * paint anything.
	 * 
	 * @see '<a href="http://www.eclipse.org/articles/article.php?file=Article-CustomDrawingTableAndTreeItems/index.html"
	 *      >Custom Drawing Table and Tree Items</a>'
	 */
	private static final class EraseListener implements Listener {

		/*
		 * Called EXTREMELY frequently. Must be as efficient as possible.
		 */
		public void handleEvent(Event event) {
			// indicate we are responsible for drawing the cell's content
			event.detail &= ~SWT.FOREGROUND;
		}
	}

	/**
	 * Selection listener for table headers that changes the sort order of a
	 * column according to the information stored in the ridget.
	 */
	private final class ColumnSortListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			TableColumn column = (TableColumn) e.widget;
			int columnIndex = column.getParent().indexOf(column);
			int direction = column.getParent().getSortDirection();
			if (columnIndex == sortedColumn) {
				if (direction == SWT.UP) {
					setSortedAscending(false);
				} else if (direction == SWT.DOWN) {
					setSortedColumn(-1);
				}
			} else if (isColumnSortable(columnIndex)) {
				setSortedColumn(columnIndex);
				if (direction == SWT.NONE) {
					setSortedAscending(true);
				}
			}
			column.getParent().showSelection();
		}
	}

}
