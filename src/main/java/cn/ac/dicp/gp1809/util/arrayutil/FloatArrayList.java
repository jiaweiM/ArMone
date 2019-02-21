/* 
 ******************************************************************************
 * File: FloatArrayList.java * * * Created on 01-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.arrayutil;

/**
 * An arraylist for primitive data of float (modified from arraylist)
 * 
 * @author Xinning
 * @version 0.1, 01-04-2009, 14:29:01
 */
public class FloatArrayList {

	/**
	 * The number of times this list has been <i>structurally modified</i>.
	 * Structural modifications are those that change the size of the list, or
	 * otherwise perturb it in such a fashion that iterations in progress may
	 * yield incorrect results.
	 * <p>
	 * 
	 * This field is used by the iterator and list iterator implementation
	 * returned by the <tt>iterator</tt> and <tt>listIterator</tt> methods. If
	 * the value of this field changes unexpectedly, the iterator (or list
	 * iterator) will throw a <tt>ConcurrentModificationException</tt> in
	 * response to the <tt>next</tt>, <tt>remove</tt>, <tt>previous</tt>,
	 * <tt>set</tt> or <tt>add</tt> operations. This provides <i>fail-fast</i>
	 * behavior, rather than non-deterministic behavior in the face of
	 * concurrent modification during iteration.
	 * <p>
	 * 
	 * <b>Use of this field by subclasses is optional.</b> If a subclass wishes
	 * to provide fail-fast iterators (and list iterators), then it merely has
	 * to increment this field in its <tt>add(int, Object)</tt> and
	 * <tt>remove(int)</tt> methods (and any other methods that it overrides
	 * that result in structural modifications to the list). A single call to
	 * <tt>add(int, Object)</tt> or <tt>remove(int)</tt> must add no more than
	 * one to this field, or the iterators (and list iterators) will throw bogus
	 * <tt>ConcurrentModificationExceptions</tt>. If an implementation does not
	 * wish to provide fail-fast iterators, this field may be ignored.
	 */
	protected transient int modCount = 0;

	/**
	 * The array buffer into which the elements of the ArrayList are stored. The
	 * capacity of the ArrayList is the length of this array buffer.
	 */
	protected transient float[] elementData;

	/**
	 * The size of the ArrayList (the number of elements it contains).
	 * 
	 * @serial
	 */
	private int size;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the list.
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public FloatArrayList(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
			        + initialCapacity);
		this.elementData = new float[initialCapacity];
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public FloatArrayList() {
		this(10);
	}

	/**
	 * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's
	 * current size. An application can use this operation to minimize the
	 * storage of an <tt>ArrayList</tt> instance.
	 */
	public void trimToSize() {
		modCount++;
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			float oldData[] = elementData;
			elementData = new float[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary,
	 * to ensure that it can hold at least the number of elements specified by
	 * the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	public void ensureCapacity(int minCapacity) {
		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			float oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new float[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this list has no elements.
	 * 
	 * @return <tt>true</tt> if this list has no elements; <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * 
	 * @param elem
	 *            element whose presence in this List is to be tested.
	 * @return <code>true</code> if the specified element is present;
	 *         <code>false</code> otherwise.
	 */
	public boolean contains(float elem) {
		return indexOf(elem) >= 0;
	}

	/**
	 * Searches for the first occurence of the given argument, testing for
	 * equality using the <tt>equals</tt> method.
	 * 
	 * @param elem
	 *            an object.
	 * @return the index of the first occurrence of the argument in this list;
	 *         returns <tt>-1</tt> if the object is not found.
	 * @see Object#equals(Object)
	 */
	public int indexOf(float elem) {
		if (Double.isNaN(elem)) {
			for (int i = 0; i < size; i++)
				if (Double.isNaN(elementData[i]))
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (elem == elementData[i])
					return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in this
	 * list.
	 * 
	 * @param elem
	 *            the desired element.
	 * @return the index of the last occurrence of the specified object in this
	 *         list; returns -1 if the object is not found.
	 */
	public int lastIndexOf(float elem) {
		if (Double.isNaN(elem)) {
			for (int i = size - 1; i >= 0; i--)
				if (Double.isNaN(elementData[i]))
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (elem == elementData[i])
					return i;
		}
		return -1;
	}

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order.
	 * 
	 * @return an array containing all of the elements in this list in the
	 *         correct order.
	 */
	public float[] toArray() {
		float[] result = new float[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	/**
	 * Return the double array of this float array list
	 * 
	 * @return
	 */
	public double[] toDoubleArray() {
		double[] result = new double[size];

		for (int i = 0; i < size; i++) {
			result[i] = this.elementData[i];
		}

		return result;
	}

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *            index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public float get(int index) {
		RangeCheck(index);

		return elementData[index];
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param index
	 *            index of element to replace.
	 * @param element
	 *            element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range
	 *             <tt>(index &lt; 0 || index &gt;= size())</tt>.
	 */
	public float set(int index, float element) {
		RangeCheck(index);

		float oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param o
	 *            element to be appended to this list.
	 * @return insert position
	 */
	public int add(float o) {
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = o;
		return size - 1;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted.
	 * @param element
	 *            element to be inserted.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range
	 *             <tt>(index &lt; 0 || index &gt; size())</tt>.
	 */
	public void add(int index, float element) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
			        + size);

		ensureCapacity(size + 1); // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1, size
		        - index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 * 
	 * @param index
	 *            the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public float remove(int index) {
		RangeCheck(index);

		modCount++;
		float oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
			        numMoved);

		size--;

		return oldValue;
	}

	/**
	 * Removes a single instance of the specified element from this list, if it
	 * is present (optional operation). More formally, removes an element
	 * <tt>e</tt> such that <tt>(o==null ? e==null :
	 * o.equals(e))</tt>, if the list contains one or more such elements.
	 * Returns <tt>true</tt> if the list contained the specified element (or
	 * equivalently, if the list changed as a result of the call).
	 * <p>
	 * 
	 * @param o
	 *            element to be removed from this list, if present.
	 * @return <tt>true</tt> if the list contained the specified element.
	 */
	public boolean remove(float o) {
		if (Double.isNaN(o)) {
			for (int index = 0; index < size; index++)
				if (Double.isNaN(elementData[index])) {
					fastRemove(index);
					return true;
				}
		} else {
			for (int index = 0; index < size; index++)
				if (o == elementData[index]) {
					fastRemove(index);
					return true;
				}
		}
		return false;
	}

	/*
	 * Private remove method that skips bounds checking and does not return the
	 * value removed.
	 */
	private void fastRemove(int index) {
		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
			        numMoved);
		size--;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear() {
		modCount++;

		size = 0;
	}

	public boolean addAll(float[] a) {
		int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Appends all of the elements in the specified Collection to the end of
	 * this list, in the order that they are returned by the specified
	 * Collection's Iterator. The behavior of this operation is undefined if the
	 * specified Collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified Collection is this list, and this list is nonempty.)
	 * 
	 * @param c
	 *            the elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws NullPointerException
	 *             if the specified collection is null.
	 */
	public boolean addAll(FloatArrayList c) {
		float[] a = c.toArray();
		return addAll(a);
	}

	/**
	 * Inserts all of the elements in the specified Collection into this list,
	 * starting at the specified position. Shifts the element currently at that
	 * position (if any) and any subsequent elements to the right (increases
	 * their indices). The new elements will appear in the list in the order
	 * that they are returned by the specified Collection's iterator.
	 * 
	 * @param index
	 *            index at which to insert first element from the specified
	 *            collection.
	 * @param c
	 *            elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range <tt>(index
	 * 		  &lt; 0 || index &gt; size())</tt>.
	 * @throws NullPointerException
	 *             if the specified Collection is null.
	 */
	public boolean addAll(int index, FloatArrayList c) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
			        + size);

		float[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount

		int numMoved = size - index;
		if (numMoved > 0)
			System.arraycopy(elementData, index, elementData, index + numNew,
			        numMoved);

		System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Removes from this List all of the elements whose index is between
	 * fromIndex, inclusive and toIndex, exclusive. Shifts any succeeding
	 * elements to the left (reduces their index). This call shortens the list
	 * by <tt>(toIndex - fromIndex)</tt> elements. (If
	 * <tt>toIndex==fromIndex</tt>, this operation has no effect.)
	 * 
	 * @param fromIndex
	 *            index of first element to be removed.
	 * @param toIndex
	 *            index after last element to be removed.
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = size - toIndex;
		System
		        .arraycopy(elementData, toIndex, elementData, fromIndex,
		                numMoved);

		size = size - (toIndex - fromIndex);

	}

	/**
	 * Check if the given index is in range. If not, throw an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
			        + size);
	}

}
