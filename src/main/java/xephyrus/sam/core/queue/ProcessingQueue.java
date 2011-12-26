package xephyrus.sam.core.queue;

import xephyrus.sam.core.Payload;
import xephyrus.sam.core.ProcessInfo;

/**
 * Defines a queue for tracking items being processed and waiting to be processed.  Each
 * instantiated queue is specific to the {@link xephyrus.sam.core.StateMachine} it supports.
 *
 * @param <S>
 *   The states of the {@link xephyrus.sam.core.StateMachine} for this queue.
 * @param <P>
 *   The {@link Payload} of this {@link xephyrus.sam.core.StateMachine} for this queue.
 */
public interface ProcessingQueue<S extends Enum, P extends Payload>
{
  /**
   * Removes all items from the queue.
   */
  void clear ();

  /**
   * Reports the number of items in the queue.
   *
   * @return The count of items currently enqueued.
   */
  int getCount ();

  /**
   * Reports whether the specified item is currently in the queue.
   *
   * @param item
   *   The item to check.
   * @return
   *   If the item is in the queue, true is returned; otherwise false.
   */
  boolean isContained (ProcessInfo<S, P> item);

  /**
   * <p>
   *   Reports whether any items in the queue are currently <i>ready</i>.  <i>Ready</i> is a magical
   *   attribute which allows a queue implementation to hold onto enqueued items until those items
   *   are deemed ready to be processed.
   * </p><p>
   *   If a queue implementation does not have any concept of <i>ready</i>, this method should
   *   return true when {@link #getCount()} &gt; 0.
   * </p>
   *
   * @return
   *   If any items are ready to be processed, true is returned; otherwise false.  Note that
   *   this may or may not be the same as {@link #getCount()} &gt; 0.
   */
  boolean isReady ();

  /**
   * Provides the item which will be provided next time {@link #pop()} is called, but without
   * removing it from the queue.
   *
   * @return
   *   The next item to be popped from the queue.
   */
  ProcessInfo<S,P> peek ();

  /**
   * Provides the specified item in the queue without removing it from the queue.
   *
   * @param index
   *   The index of the item to be provided.
   * @return
   *   The specified item.
   * @throws ProcessingQueueException
   *   If no item exists at the specified index, this will be thrown.
   */
  ProcessInfo<S,P> peek (int index)
    throws ProcessingQueueException;

  /**
   * Provides the next item in the queue and removes it from the queue.  As long as
   * {@link #getCount()} &gt; 0, this will return an item.
   *
   * @return
   *   The next item from the queue.
   */
  ProcessInfo<S,P> pop ();

  /**
   * <p>
   *   Provides the next <i>readt</i> item in the queue and removes it from the queue.  If there
   *   are no items that are currently <i>ready</i>, this may return null, even if
   *   {@link #getCount()} &gt; 0.
   * </p><p>
   *   <i>Ready</i> is a magical attribute which allows a queue implementation to hold onto
   *   enqueued items until those items are deemed ready to be processed.
   * </p>
   * @return
   *   The next <i>ready</i> iem from the queue.
   */
  ProcessInfo<S,P> popReady ();

  /**
   * <p>
   *   Adds an item into the queue.  This method is used to re-add items which have previously been
   *   in the queue.  For example, while an item is being processed it is removed from the queue,
   *   and when the processing for that state is complete the item is re-added to the queue with
   *   it's new state.
   * </p><p>
   *   The first time an item is added to a queue, the {@link #queue(xephyrus.sam.core.ProcessInfo)}
   *   method is used.
   * </p>
   *
   * @param item
   *   The item to be added to the queue.
   * @throws ProcessingQueueException
   *   If any problems are encountered while adding this item to the queue, this will be thrown
   *   with an appropriate message.
   */
  void push (ProcessInfo<S,P> item)
    throws ProcessingQueueException;

  /**
   * <p>
   *   Adds an item into the queue.  This method is used to add new items to the queue.
   * </p><p>
   *   When an item is re-added to the queue during processing, the
   *   {@link #push(xephyrus.sam.core.ProcessInfo)} method is used.
   * </p>
   * @param item
   *   The item to be added to the queue.
   * @throws ProcessingQueueException
   *   If any problems are encountered while adding this item to the queue, this will be thrown
   *   with an appropriate message.
   */
  void queue (ProcessInfo<S,P> item)
    throws ProcessingQueueException;

  /**
   * Removes the item at the specified index from the queue.
   *
   * @param index
   *   The index of the item to be removed.
   * @return
   *   The removed item.
   * @throws ProcessingQueueException
   *   If any problems are encountered while removing this item from the queue, or if no item
   *   exists at the specified index this will be thrown with an appropriate message.
   */
  ProcessInfo<S,P> remove (int index)
    throws ProcessingQueueException;
}
