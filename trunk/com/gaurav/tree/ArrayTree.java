/*
 * Copyright 2010 Gaurav Saxena
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.gaurav.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The nodes in this class always have a particular number of children. It is not possible to add more children 
 * than the number provided in the constructor. Since the children are numbered, it is possible to access them
 * by index. This class serves as a base class to trees which are used to keep order among its elements e.g. BST
 * 
 * add(parent, child) adds child to the first available slot. Thus, it is better to add nodes using 
 * add(parent, child, index)
 * @author Gaurav Saxena
 *
 * @param <E>
 */
public class ArrayTree<E> implements NumberedTree<E>, Cloneable {
	private ArrayList<E> nodeList = new ArrayList<E>();
	private ArrayList<Integer> parentList = new ArrayList<Integer>();
	private ArrayList<int[]> childrenArray = new ArrayList<int[]>();
	private int size = 0;
	private int depth = 0;
	private int maxChildren;
	
	public ArrayTree(int maxChildren) {
		this.maxChildren = maxChildren;
	}
	
	@Override
	public boolean add(E e) {
		try{
			if(nodeList.isEmpty())
				return add(null, e);
			else
				return add(nodeList.get(0), e);
		}
		catch(NodeNotFoundException ex)
		{
			return false;
		}
	}
	@Override
	public boolean add(E parent, E child) throws NodeNotFoundException {
		checkNode(child);
		if(parent == null)
		{
			if(nodeList.isEmpty())
			{
				addRoot(child);
				return true;
			}
			else
				throw new IllegalArgumentException("parent cannot be null except for root element");
		}
		int	parentIndex = nodeList.indexOf(parent);
		if(parentIndex > -1)
		{
			int emptySlot;
			if(nodeList.indexOf(child) == -1 && (emptySlot = getEmptySlot(childrenArray.get(parentIndex))) > -1)
			{
				addChild(child, parentIndex, emptySlot);
				return true;
			}
			else
				return false;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public boolean add(E parent, E child, int index) throws NodeNotFoundException {
		checkNode(child);
		if(parent == null)
		{
			if(nodeList.isEmpty())
			{
				addRoot(child);
				return true;
			}
			else
				throw new IllegalArgumentException("parent cannot be null except for root element");
		}
		int	parentIndex = nodeList.indexOf(parent);
		if(parentIndex > -1)
		{
			if(nodeList.indexOf(child) == -1 && (index >= 0 && index < maxChildren))
			{
				addChild(child, parentIndex, index);
				return true;
			}
			else
				return false;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean retVal = false;
		for (Iterator<? extends E> iterator = c.iterator(); iterator.hasNext();)
			retVal |= add(iterator.next());
		return retVal;
	}
	@Override
	public boolean addAll(E parent, Collection<? extends E> c) {
		try{
			for (Iterator<? extends E> iterator = c.iterator(); iterator.hasNext();)
				add(parent, iterator.next());
			return true;
		}
		catch(NodeNotFoundException ex)
		{
			return false;
		}
	}
	@Override
	public E child(E parent, int index) throws NodeNotFoundException
	{
		checkNode(parent);
		int parentIndex = nodeList.indexOf(parent);
		int childIndex;
		if(parentIndex > -1)
		{
			if((childIndex = childrenArray.get(parentIndex)[index]) > -1)
				return nodeList.get(childIndex);
			else
				return null;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public List<E> children(E e) throws NodeNotFoundException
	{
		checkNode(e);
		int index = nodeList.indexOf(e);
		if(index > -1)
		{
			ArrayList<E> children = new ArrayList<E>();
			for (int i = 0; i < childrenArray.get(index).length; i++)
				if(childrenArray.get(index)[i] > -1)
					children.add(nodeList.get(childrenArray.get(index)[i]));
			return children;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public void clear() {
		nodeList.clear();
		parentList.clear();
		childrenArray.clear();
		size = 0;
		depth = 0;
	}
	@Override
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		ArrayTree<E> v = null;
		try {
			v = (ArrayTree<E>) super.clone();
			v.nodeList = (ArrayList<E>) nodeList.clone();
			v.parentList = (ArrayList<Integer>) parentList.clone();
			v.childrenArray = new ArrayList<int[]>();
			for(int i = 0; i < childrenArray.size(); i++)
				v.childrenArray.add(Arrays.copyOf(childrenArray.get(i), childrenArray.get(i).length));
			    	
		} catch (CloneNotSupportedException e) {
			//This should't happen because we are cloneable
		}
		return v;
	}
	@Override
	public E commonAncestor(E node1, E node2) throws NodeNotFoundException {
		checkNode(node1);
		checkNode(node2);
		int height1 = 0;
		E e1 = node1; 
		while(e1 != null)
		{
			height1++;
			e1 = parent(e1);
		}
		int height2 = 0;
		E e2 = node2; 
		while(e2 != null)
		{
			height2++;
			e2 = parent(e2);
		}
		if(height1 > height2)
		{
			while(height1 - height2 > 0)
			{
				node1 = parent(node1);
				height1--;
			}
		}
		else
		{
			while(height2 - height1 > 0)
			{
				node2 = parent(node2);
				height2--;
			}
		}
		while(node1 != null && !node1.equals(node2))
		{
			node1 = parent(node1);
			node2 = parent(node2);
		}
		return node1;
	}
	@Override
	public boolean contains(Object o) {
		if(o == null)
			return false;
		else
			return nodeList.indexOf(o) > -1;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return nodeList.containsAll(c);
	}
	@Override
	public int depth() {
		return depth;
	}
	public List<E> inorderOrderTraversal()
	{
		return inorderOrderTraversal(0, new ArrayList<E>());
	}
	@Override
	public boolean isAncestor(E node, E child) throws NodeNotFoundException {
		checkNode(child);
		child = parent(child);
		if(node != null)//if parent is root, it has to be an ancestor
		{
			while(child != null)
			{
				if(child.equals(node))
					return true;
				else
					child = parent(child);
			}
		}
		return true;
	}
	@Override
	public boolean isDescendant(E parent, E node) throws NodeNotFoundException {
		checkNode(node);
		int index = nodeList.indexOf(node);
		E child = parent(node);
		if(index > -1)
		{
			while(child != null)
			{
				if(child.equals(parent))
					return true;
				else
					child = parent(child);
			}
			return false;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	@Override
	public Iterator<E> iterator() {
		return getCurrentList().iterator();
	}
	@Override
	public List<E> leaves() {
		LinkedList<E> list = new LinkedList<E>();
		if(!nodeList.isEmpty())
		{
			E e;
			for(int i = nodeList.size() - 1; i >= 0; i--)
				if(isChildrenArrayEmpty(childrenArray.get(i))&& (e = nodeList.get(i)) != null)
					//checking for null because after deleting the list may contain null
					list.addFirst(e);
		}
		return list;
	}
	@Override
	public List<E> levelOrderTraversal()
	{
		if(nodeList.isEmpty())
			return new ArrayList<E>();
		else
		{
			LinkedList<Integer> queue = new LinkedList<Integer>();
			queue.add(0);
			return levelOrderTraversal(new ArrayList<E>(), queue);
		}
	}
	@Override
	public E parent(E e) throws NodeNotFoundException
	{
		checkNode(e);
		int index = nodeList.indexOf(e);
		if(index == 0)
			return null;
		else if(index > 0)
			return nodeList.get(parentList.get(index));
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public List<E> postOrderTraversal()
	{
		return postOrderTraversal(0, new ArrayList<E>());
	}
	public List<E> preOrderTraversal()
	{
		return preOrderTraversal(0, new ArrayList<E>());
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		checkNode((E)o);
		int i = nodeList.indexOf(o);
		if(i > -1)
			return remove(i);
		else
			return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean retVal = false;
		for (Iterator<?> iterator = c.iterator(); iterator.hasNext();)
			retVal |= remove(iterator.next());
		return retVal;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean retVal = false;
		for (Iterator<E> iterator = nodeList.iterator(); iterator.hasNext();) {
			E object = iterator.next();
			if(!c.contains(object))
				retVal |= remove(object);
		}
		return retVal;
	}

	@Override
	public E root() {
		if(nodeList.isEmpty())
			return null;
		else
			return nodeList.get(0);
	}
	@Override
	public List<E> siblings(E e) throws NodeNotFoundException
	{
		checkNode(e);
		E parent = parent(e);
		if(parent != null)
		{
			List<E> children = children(parent);
			children.remove(e);
			return children;
		}
		else
			return new ArrayList<E>();
	}
	@Override
	public int size() {
		return size;
	}
	@Override
	public Object[] toArray() {
		return getCurrentList().toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return getCurrentList().toArray(a);
	}

	private void addChild(E child, int parentIndex, int childIndex) {
		nodeList.add(child);
		parentList.add(parentIndex);
		childrenArray.get(parentIndex)[childIndex] = nodeList.size() - 1;
		int[] children = new int[maxChildren];
		Arrays.fill(children, -1);
		childrenArray.add(children);
		size++;
		int currentDepth = 2;
		while(parentIndex != 0)
		{
			parentIndex = parentList.get(parentIndex);
			currentDepth++;
		}
		depth = Math.max(currentDepth, depth);
	}

	private void addRoot(E child) {
		nodeList.add(child);
		parentList.add(-1);
		int[] children = new int[maxChildren];
		Arrays.fill(children, -1);
		childrenArray.add(children);
		size++;
		depth++;
	}

	private void checkNode(E child) {
		if(child == null)
			throw new IllegalArgumentException("null nodes are not allowed");
	}

	private List<E> getCurrentList() {
		List<E> nodes = new ArrayList<E>();
		for(int i = 0;i < nodes.size(); i++)
			if(nodeList.get(i) != null)
				nodes.add(nodeList.get(i));
		return nodes;
	}
	private int getEmptySlot(int[] children) {
		for (int i = 0; i < children.length; i++)
			if(children[i] == -1)
				return i;
		return -1;
	}
	private List<E> inorderOrderTraversal(int nodeIndex, ArrayList<E> list) {
		int[] children = childrenArray.get(nodeIndex);
		if(children.length > 0)
		{
			int i = 0;
			for(; i < (int)Math.ceil((double)children.length / 2); i++)
				if(children[i] > -1)
					inorderOrderTraversal(children[i], list);
			list.add(nodeList.get(nodeIndex));
			for(; i < children.length; i++)
				if(children[i] > -1)
					inorderOrderTraversal(children[i], list);
		}
		else
			list.add(nodeList.get(nodeIndex));
		return list;
	}
	private boolean isChildrenArrayEmpty(int[] children) {
		for (int i = 0; i < children.length; i++)
			if(children[i] != -1)
				return false;
		return true;
	}
	private List<E> levelOrderTraversal(ArrayList<E> list, LinkedList<Integer> queue) {
		if(!queue.isEmpty())
		{
			list.add(nodeList.get(queue.getFirst()));
			int[] children = childrenArray.get(queue.getFirst());
			for(int i = 0; i < children.length; i++)
				if(children[i] > -1)
					queue.add(children[i]);
			queue.remove();
			levelOrderTraversal(list, queue);
		}
		return list;
	}
	private List<E> postOrderTraversal(int nodeIndex, ArrayList<E> list) {
		int[] children = childrenArray.get(nodeIndex);
		for(int i = 0; i < children.length; i++)
			if(children[i] > -1)
				postOrderTraversal(children[i], list);
		if(nodeList.get(nodeIndex) != null)
			list.add(nodeList.get(nodeIndex));
		return list;
	}
	private List<E> preOrderTraversal(int nodeIndex, ArrayList<E> list) {
		if(nodeList.get(nodeIndex) != null)
			list.add(nodeList.get(nodeIndex));
		int[] children = childrenArray.get(nodeIndex);
		for(int i = 0; i < children.length; i++)
			if(children[i] > -1)
				preOrderTraversal(children[i], list);
		return list;
	}
	private boolean remove(int index) {
		if(index > -1)
		{
			Integer parentIndex = parentList.set(index, -1);
			for(int i = 0; i < childrenArray.get(parentIndex).length; i++)
				if(childrenArray.get(parentIndex)[i] == index)
					childrenArray.get(parentIndex)[i] = -1;
			nodeList.set(index, null);
			size--;
			int[] children = childrenArray.get(index);
			for (int j = 0; j < children.length; j++) 
				remove(children[j]);
			Arrays.fill(childrenArray.get(index), -1);
			return true;
		}
		else
			return false;
	}
}
