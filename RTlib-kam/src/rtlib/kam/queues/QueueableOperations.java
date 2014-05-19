package rtlib.kam.queues;



public interface QueueableOperations<T>
{
	public Queue<T> getCurrentQueue();

	public void setCurrentQueue(Queue<T> pQueue);
}
