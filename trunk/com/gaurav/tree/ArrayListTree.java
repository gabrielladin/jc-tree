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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This implementation of tree interface is done using {@link ArrayList}as underlying data structure. 
 * As a result, children are maintained in insertion order under their respective parents 
 * @author Gaurav Saxena
 *
 * @param <E>
 */
public class ArrayListTree<E> implements Tree<E>, Serializable, Cloneable{
	private static final long serialVersionUID = 9188932537753945512L;
	private ArrayList<E> nodeList = new ArrayList<E>();
	private ArrayList<Integer> parentList = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> childrenList = new ArrayList<ArrayList<Integer>>();
	private int size = 0;
	private int depth = 0;
	
	public List<E> preOrderTraversal()
	{
		return preOrderTraversal(0, new ArrayList<E>());
	}
	private List<E> preOrderTraversal(int nodeIndex, ArrayList<E> list) {
		if(nodeList.get(nodeIndex) != null)
			list.add(nodeList.get(nodeIndex));
		ArrayList<Integer> children = childrenList.get(nodeIndex);
		for(int i = 0; i < children.size(); i++)
			preOrderTraversal(children.get(i), list);
		return list;
	}
	public List<E> postOrderTraversal()
	{
		return postOrderTraversal(0, new ArrayList<E>());
	}
	private List<E> postOrderTraversal(int nodeIndex, ArrayList<E> list) {
		ArrayList<Integer> children = childrenList.get(nodeIndex);
		for(int i = 0; i < children.size(); i++)
			postOrderTraversal(children.get(i), list);
		if(nodeList.get(nodeIndex) != null)
			list.add(nodeList.get(nodeIndex));
		return list;
	}
	public List<E> inorderOrderTraversal()
	{
		return inorderOrderTraversal(0, new ArrayList<E>());
	}
	private List<E> inorderOrderTraversal(int nodeIndex, ArrayList<E> list) {
		ArrayList<Integer> children = childrenList.get(nodeIndex);
		if(children.size() > 0)
		{
			for(int i = 0; i < children.size(); i++)
			{
				if(i >= children.size() / 2 && nodeList.get(nodeIndex) != null)
					list.add(nodeList.get(nodeIndex));
				inorderOrderTraversal(children.get(i), list);
			}
		}
		else
			list.add(nodeList.get(nodeIndex));
		return list;
	}
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
	private List<E> levelOrderTraversal(ArrayList<E> list, LinkedList<Integer> queue) {
		if(!queue.isEmpty())
		{
			list.add(nodeList.get(queue.getFirst()));
			ArrayList<Integer> children = childrenList.get(queue.getFirst());
			for(int i = 0; i < children.size(); i++)
				queue.add(children.get(i));
			queue.remove();
			levelOrderTraversal(list, queue);
		}
		return list;
	}
	public List<E> children(E e) throws NodeNotFoundException
	{
		checkNode(e);
		int index = nodeList.indexOf(e);
		if(index > -1)
		{
			ArrayList<E> children = new ArrayList<E>();
			for (Iterator<Integer> iterator = childrenList.get(index).iterator(); iterator.hasNext();) {
				children.add(nodeList.get(iterator.next()));
			}
			return children;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	@Override
	public Collection<E> leaves() {
		if(nodeList.isEmpty())
			return new ArrayList<E>();
		else
		{
			LinkedList<Integer> queue = new LinkedList<Integer>();
			queue.add(0);
			return leaves(new ArrayList<E>(), queue);
		}
	}
	private Collection<E> leaves(ArrayList<E> list, LinkedList<Integer> queue) {
		if(!queue.isEmpty())
		{
			ArrayList<Integer> children = childrenList.get(queue.getFirst());
			if(children.isEmpty())
				list.add(nodeList.get(queue.getFirst()));
			for(int i = 0; i < children.size(); i++)
				queue.add(children.get(i));
			queue.remove();
			leaves(list, queue);
		}
		return list;
	}
	public Collection<E> siblings(E e) throws NodeNotFoundException
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
			throw new NodeNotFoundException("parent could not be found for the child object");		
	}
	public E parent(E e) throws NodeNotFoundException
	{
		if(e == null)
			throw new IllegalArgumentException("Null nodes are not allowed");
		int index = nodeList.indexOf(e);
		if(index == 0)
			return null;
		else if(index > 0)
			return nodeList.get(parentList.get(index));
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	public boolean add(E parent, E child) throws NodeNotFoundException {
		checkNode(child);
		if(parent == null)
		{
			if(nodeList.isEmpty())
			{
				nodeList.add(child);
				parentList.add(-1);
				childrenList.add(new ArrayList<Integer>());
				size++;
				depth++;
				return true;
			}
			else
				throw new IllegalArgumentException("parent cannot be null except for root element");
		}
		int	parentIndex = nodeList.indexOf(parent);
		if(parentIndex > -1)
		{
			if(nodeList.indexOf(child) == -1)
			{
				nodeList.add(child);
				parentList.add(parentIndex);
				childrenList.get(parentIndex).add(nodeList.size() - 1);
				childrenList.add(new ArrayList<Integer>());
				size++;
				int currentDepth = 1;
				while(parentIndex != 0)
				{
					parentIndex = parentList.get(parentIndex);
					currentDepth++;
				}
				depth = Math.max(currentDepth, depth);
				return true;
			}
			else
				return false;
		}
		else
			throw new NodeNotFoundException("No node was found for object");
	}
	private void checkNode(E child) {
		if(child == null)
			throw new IllegalArgumentException("null nodes are not allowed");
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
	public boolean addAll(Collection<? extends E> c) {
		boolean retVal = false;
		for (Iterator<? extends E> iterator = c.iterator(); iterator.hasNext();)
			retVal |= add(iterator.next());
		return retVal;
	}

	@Override
	public void clear() {
		nodeList.clear();
		parentList.clear();
		childrenList.clear();
		size = 0;
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
	public boolean isEmpty() {
		return size() > 0;
	}

	@Override
	public Iterator<E> iterator() {
		return getCurrentList().iterator();
	}

	private List<E> getCurrentList() {
		List<E> nodes = new ArrayList<E>();
		for(int i = 0;i < nodes.size(); i++)
			if(nodeList.get(i) != null)
				nodes.add(nodeList.get(i));
		return nodes;
	}
	@Override
	public boolean remove(Object o) {
		int i = nodeList.indexOf(o);
		if(i > -1)
			return remove(i);
		else
			return false;
	}
	private boolean remove(int index) {
		if(index > -1)
		{
			Integer parentIndex = parentList.set(index, -1);
			childrenList.get(parentIndex).remove(Integer.valueOf(index));
			nodeList.set(index, null);
			size--;
			ArrayList<Integer> children = childrenList.get(index);
			for (int j = 0; j < children.size(); j++) 
				remove(children.get(j).intValue());
			childrenList.get(index).clear();
			return true;
		}
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
	public Object[] toArray() {
		return getCurrentList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getCurrentList().toArray(a);
	}

	@Override
	public int size() {
		return size;
	}
	@SuppressWarnings("unchecked")
	public Object clone()
	{
	    ArrayListTree<E> v = null;
		try {
			v = (ArrayListTree<E>) super.clone();
			v.nodeList = (ArrayList<E>) nodeList.clone();
			v.parentList = (ArrayList<Integer>) parentList.clone();
			v.childrenList = new ArrayList<ArrayList<Integer>>();
			for(int i = 0; i < childrenList.size(); i++)
				v.childrenList.add((ArrayList<Integer>) childrenList.get(i).clone());
			    	
		} catch (CloneNotSupportedException e) {
			//This should't happen because we are cloneable
		}
		return v;
	}
	@Override
	public int depth() {
		return depth;
	}
	@Override
	public E root() {
		if(nodeList.isEmpty())
			return null;
		else
			return nodeList.get(0);
	}
}