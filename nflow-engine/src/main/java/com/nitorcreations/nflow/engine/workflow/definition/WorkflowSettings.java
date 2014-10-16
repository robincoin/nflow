package com.nitorcreations.nflow.engine.workflow.definition;

import static java.lang.Math.min;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.joda.time.DateTime.now;

import org.joda.time.DateTime;
import org.springframework.core.env.Environment;

/**
 * Configuration for the workflow execution.
 */
public class WorkflowSettings {
  public final int minErrorTransitionDelay;
  public final int maxErrorTransitionDelay;
  public final int shortTransitionDelay;
  public final int immediateTransitionDelay;
  public final int maxRetries;

  WorkflowSettings(Builder builder) {
    this.minErrorTransitionDelay = builder.minErrorTransitionDelay;
    this.maxErrorTransitionDelay = builder.maxErrorTransitionDelay;
    this.shortTransitionDelay = builder.shortTransitionDelay;
    this.immediateTransitionDelay = builder.immediateTransitionDelay;
    this.maxRetries = builder.maxRetries;
  }

  /**
   * Builder for workflow settings.
   */
  public static class Builder {

    int maxErrorTransitionDelay;
    int minErrorTransitionDelay;
    int shortTransitionDelay;
    int immediateTransitionDelay;
    int maxRetries;

    /**
     * Create builder for workflow settings using default values.
     */
    public Builder() {
      this(null);
    }

    /**
     * Create builder for workflow settings using configured default values.
     *
     * @param env
     *          Spring environment.
     */
    public Builder(Environment env) {
      minErrorTransitionDelay = getIntegerProperty(env, "nflow.transition.delay.error.min.ms", (int) MINUTES.toMillis(1));
      maxErrorTransitionDelay = getIntegerProperty(env, "nflow.transition.delay.error.max.ms", (int) DAYS.toMillis(1));
      shortTransitionDelay = getIntegerProperty(env, "nflow.transition.delay.waitshort.ms", (int) SECONDS.toMillis(30));
      immediateTransitionDelay = getIntegerProperty(env, "nflow.transition.delay.immediate.ms", 0);
      maxRetries = getIntegerProperty(env, "nflow.max.state.retries", 17);
    }

    /**
     * Set maximum error transition delay.
     *
     * @param maxErrorTransitionDelay
     *          Delay in milliseconds.
     * @return this.
     */
    public Builder setMaxErrorTransitionDelay(int maxErrorTransitionDelay) {
      this.maxErrorTransitionDelay = maxErrorTransitionDelay;
      return this;
    }

    /**
     * Set minimum error transition delay.
     *
     * @param minErrorTransitionDelay
     *          Delay in milliseconds.
     * @return this.
     */
    public Builder setMinErrorTransitionDelay(int minErrorTransitionDelay) {
      this.minErrorTransitionDelay = minErrorTransitionDelay;
      return this;
    }

    /**
     * Set short transition delay.
     *
     * @param shortTransitionDelay
     *          Delay in milliseconds.
     * @return this.
     */
    public Builder setShortTransitionDelay(int shortTransitionDelay) {
      this.shortTransitionDelay = shortTransitionDelay;
      return this;
    }

    /**
     * Set immediate transition delay.
     *
     * @param immediateTransitionDelay
     *          Delay in milliseconds.
     * @return this.
     */
    public Builder setImmediateTransitionDelay(int immediateTransitionDelay) {
      this.immediateTransitionDelay = immediateTransitionDelay;
      return this;
    }

    /**
     * Set maximum retry attempts.
     *
     * @param maxRetries
     *          Maximum number of retries.
     * @return this.
     */
    public Builder setMaxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
      return this;
    }

    private int getIntegerProperty(Environment env, String key, int defaultValue) {
      if (env != null) {
        return env.getProperty(key, Integer.class, defaultValue);
      }
      return defaultValue;
    }

    /**
     * Create workflow settings object.
     *
     * @return Workflow settings.
     */
    public WorkflowSettings build() {
      return new WorkflowSettings(this);
    }
  }

  /**
   * Return next activation time after error.
   *
   * @param retryCount
   *          Number of retry attemps.
   * @return Next activation time.
   */
  public DateTime getErrorTransitionActivation(int retryCount) {
    return now().plusMillis(calculateBinaryBackoffDelay(retryCount + 1, minErrorTransitionDelay, maxErrorTransitionDelay));
  }

  /**
   * Return activation delay based on retry attempt number.
   *
   * @param retryCount
   *          Retry attempt number.
   * @param minDelay
   *          Minimum retry delay.
   * @param maxDelay
   *          Maximum retry delay.
   * @return Delay in milliseconds.
   */
  protected int calculateBinaryBackoffDelay(int retryCount, int minDelay, int maxDelay) {
    return min(minDelay * (1 << retryCount), maxDelay);
  }

  /**
   * Return the delay before next activation after detecting a busy loop.
   *
   * @return The delay in milliseconds.
   */
  public DateTime getShortTransitionActivation() {
    return now().plusMillis(shortTransitionDelay);
  }
}