/**
 *  All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.quartz.utils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * An implementation of a CircularQueue data-structure.
 * When the number of items added exceeds the maximum capacity, items that were
 * added first are lost.
 *
 * @param <T>
 *            Type of the item's to add in this queue
 *
 * @author <a href="mailto:asanoujam@terracottatech.com">Abhishek Sanoujam</a>
 * @since 1.7
 */
public class CircularLossyQueue<T> {
    private final AtomicReference<T>[] circularArray;
    private final int maxSize;
    private int currentIndex = -1;
    private boolean isFull = false;

    /**
     * Constructs the circular queue with the specified capacity
     *
     * @param size
     */
    @SuppressWarnings("unchecked")
    public CircularLossyQueue(int size) {
        this.circularArray = new AtomicReference[size];
        for (int i = 0; i < size; i++) {
            this.circularArray[i] = new AtomicReference<>();
        }
        this.maxSize = size;
    }

    /**
     * Adds a new item
     *
     * @param newVal
     */
    public void push(T newVal) {
        int index = (++currentIndex) % maxSize;
        circularArray[index].set(newVal);
        isFull = isFull || currentIndex == maxSize;
        currentIndex = index;
    }

    /**
     * Returns an array of the current elements in the queue. The order of
     * elements is in reverse order of the order items were added.
     *
     * @param type
     * @return An array containing the current elements in the queue. The first
     *         element of the array is the tail of the queue and the last
     *         element is the head of the queue
     */
    public T[] toArray(T[] type) {
        if (type.length > maxSize) {
            throw new IllegalArgumentException("Size of array passed in cannot be greater than " + maxSize);
        }

        int curIndex = currentIndex + maxSize;
        for (int k = 0; k < type.length; k++) {
            int index = (curIndex - k) % maxSize;
            type[k] = circularArray[index].get();
        }
        return type;
    }

    /**
     * Returns value at the tail of the queue
     *
     * @return Value at the tail of the queue
     */
    public T peek() {
        if (currentIndex == -1) {
            return null;
        }
        return circularArray[currentIndex].get();
    }

    /**
     * Returns true if the queue is empty, otherwise false
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return currentIndex == -1;
    }

    /**
     * Returns the number of items currently in the queue
     *
     * @return the number of items in the queue
     */
    public int depth() {
        return isFull ? maxSize : currentIndex + 1;
    }
}
