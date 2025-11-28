package org.quartz.core;

import static org.quartz.core.JobExecutionProcessException.ProcessErrorMessage.JobExecution;
import static org.quartz.core.JobExecutionProcessException.ProcessErrorMessage.JobListenerExecution;
import static org.quartz.core.JobExecutionProcessException.ProcessErrorMessage.TriggerListenerExecution;

import org.quartz.JobExecutionContext;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;

/**
 * This exception is thrown when an error occurs during execution:
 * <ul>
 *     <li><code>Job execution</code></li>
 *     <li><code>{@link JobListener} methods</code></li>
 *     <li><code>{@link TriggerListener} methods</code></li>
 * </ul>
 * The exception ensures that the job execution context is transferred to the implementation of the error
 * handling method of the scheduler listener <code>{@link org.quartz.SchedulerListener#schedulerError(String, SchedulerException)}</code>,
 * to try to fix the problem
 */
public class JobExecutionProcessException extends SchedulerException
{
    enum ProcessErrorMessage
    {
        JobExecution("Job threw an unhandled exception"),
        JobListenerExecution("JobListener '%s' threw exception: %s."),
        TriggerListenerExecution("TriggerListener '%s' threw exception: %s.");

        private final String errorMsg;

        ProcessErrorMessage(String errorMsg)
        {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg()
        {
            return errorMsg;
        }
    }

    /** The job execution context. */
    private final JobExecutionContext jobExecutionContext;

    public JobExecutionProcessException(JobExecutionContext jobExecutionContext, Throwable cause)
    {
        super(JobExecution.errorMsg, cause);
        this.jobExecutionContext = jobExecutionContext;
    }

    public JobExecutionProcessException(JobListener listener, JobExecutionContext jobExecutionContext, Throwable cause)
    {
        super(String.format(JobListenerExecution.getErrorMsg(), listener.getName(), cause.getMessage()), cause);
        this.jobExecutionContext = jobExecutionContext;
    }

    public JobExecutionProcessException(TriggerListener listener, JobExecutionContext jobExecutionContext, Throwable cause)
    {
        super(String.format(TriggerListenerExecution.getErrorMsg(), listener.getName(), cause.getMessage()), cause);
        this.jobExecutionContext = jobExecutionContext;
    }

    public JobExecutionContext getJobExecutionContext()
    {
        return jobExecutionContext;
    }
}
