package com.example.application.views.treeGrid;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemDataProvider extends
        AbstractBackEndHierarchicalDataProvider<File, FilenameFilter> {

    private static final Comparator<File> nameComparator = (fileA, fileB) -> {
        return String.CASE_INSENSITIVE_ORDER.compare(fileA.getName(), fileB.getName());
    };

    private static final Comparator<File> sizeComparator = Comparator.comparingLong(File::length);

    private static final Comparator<File> lastModifiedComparator = Comparator.comparingLong(File::lastModified);

    private final File root;

    public FileSystemDataProvider(File root) {
        this.root = root;
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<File, FilenameFilter> query) {
        return (int) fetchChildren(query).count();
    }

    @Override
    protected Stream<File> fetchChildrenFromBackEnd(
            HierarchicalQuery<File, FilenameFilter> query) {
        final File parent = query.getParentOptional().orElse(root);
        Stream<File> filteredFiles = query.getFilter()
                .map(filter -> Stream.of(parent.listFiles(filter)))
                .orElse(Stream.of(parent.listFiles()))
                .skip(query.getOffset()).limit(query.getLimit());
        return sortFileStream(filteredFiles, query.getSortOrders());
    }

    @Override
    public boolean hasChildren(File item) {
        return item.list() != null && item.list().length > 0;
    }

    private Stream<File> sortFileStream(Stream<File> fileStream,
                                        List<QuerySortOrder> sortOrders) {

        if (sortOrders.isEmpty()) {
            return fileStream;
        }

        List<Comparator<File>> comparators = sortOrders.stream()
                .map(sortOrder -> {
                    Comparator<File> comparator = null;
                    if (sortOrder.getSorted().equals("file-name")) {
                        comparator = nameComparator;
                    } else if (sortOrder.getSorted().equals("file-size")) {
                        comparator = sizeComparator;
                    } else if (sortOrder.getSorted().equals("file-last-modified")) {
                        comparator = lastModifiedComparator;
                    }
                    if (comparator != null && sortOrder
                            .getDirection() == SortDirection.DESCENDING) {
                        comparator = comparator.reversed();
                    }
                    return comparator;
                }).filter(Objects::nonNull).collect(Collectors.toList());

        if (comparators.isEmpty()) {
            return fileStream;
        }

        Comparator<File> first = comparators.remove(0);
        Comparator<File> combinedComparators = comparators.stream()
                .reduce(first, Comparator::thenComparing);
        return fileStream.sorted(combinedComparators);
    }

    static class IconTreeGrid<T> extends TreeGrid<T> {

        IconTreeGrid(HierarchicalDataProvider<T, ?> dataProvider) {
            super(dataProvider);
        }

        public Column<T> addHierarchyColumn(Icon icon, ValueProvider<T, ?> valueProvider) {
            final Column<T> column = addColumn(TemplateRenderer.<T>of(
                            "<vaadin-grid-tree-toggle leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>" +
                                    "<iron-icon icon='[[item.icon]]' style='margin-right: 0.2em; [[item.style]]'></iron-icon>[[item.name]]" +
                                    "</vaadin-grid-tree-toggle>")
                    .withProperty("leaf", item -> !getDataCommunicator().hasChildren(item))
                    .withProperty("icon", item -> icon.getElement().getAttribute("icon"))
                    .withProperty("style", item -> icon.getElement().getAttribute("style"))
                    .withProperty("name", item -> String.valueOf(valueProvider.apply(item))));
            final SerializableComparator<T> comparator =  (a, b) ->
                    compareMaybeComparables(valueProvider.apply(a), valueProvider.apply(b));
            column.setComparator(comparator);
            return column;
        }

    }
}