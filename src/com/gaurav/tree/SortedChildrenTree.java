package com.gaurav.tree;

import java.util.Collections;
import java.util.List;

public class SortedChildrenTree<E extends Comparable<E>> extends ArrayListTree<E> {
	/**
	 * This adds the child at a insertion position defined by {@link Collections#binarySearch(List, Object)}
	 * @see com.gaurav.tree.ArrayListTree#getChildAddPosition(java.util.List, java.lang.Object)
	 */
	@Override
	protected int getChildAddPosition(List<E> children, E newChild) {
		return -Collections.binarySearch(children, newChild) - 1;
	}
}
