package io.nflow.engine.workflow.definition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.joda.time.DateTimeUtils.currentTimeMillis;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkflowSettingsTest {
  DateTime now = new DateTime(2014, 10, 22, 20, 44, 0);

  @BeforeEach
  public void setup() {
    DateTimeUtils.setCurrentMillisFixed(now.getMillis());
  }

  @AfterEach
  public void teardown() {
    DateTimeUtils.setCurrentMillisSystem();
  }

  @Test
  public void verifyConstantDefaultValues() {
    WorkflowSettings s = new WorkflowSettings.Builder().build();
    assertThat(s.immediateTransitionDelay, is(0));
    assertThat(s.shortTransitionDelay, is(30000));
    long delta = s.getShortTransitionActivation().getMillis() - currentTimeMillis() - 30000;
    assertThat(delta, greaterThanOrEqualTo(-1000L));
    assertThat(delta, lessThanOrEqualTo(0L));
    assertThat(s.historyDeletableAfterHours, is(nullValue()));
    assertThat(s.defaultPriority, is((short) 0));
  }

  @Test
  public void errorTransitionDelayIsBetweenMinAndMaxDelay() {
    int maxDelay = 1_000_000;
    int minDelay = 1000;
    WorkflowSettings s = new WorkflowSettings.Builder().setMinErrorTransitionDelay(minDelay).setMaxErrorTransitionDelay(maxDelay).build();
    long prevDelay = 0;
    for(int retryCount = 0 ; retryCount < 100 ; retryCount++) {
      long delay  = s.getErrorTransitionActivation(retryCount).getMillis() - now.getMillis();
      assertThat(delay, greaterThanOrEqualTo((long)minDelay));
      assertThat(delay, lessThanOrEqualTo((long)maxDelay));
      assertThat(delay, greaterThanOrEqualTo(prevDelay));
      prevDelay = delay;
    }
  }

  @Test
  public void getMaxSubsequentStateExecutionsReturns100ByDefault() {
    WorkflowSettings s = new WorkflowSettings.Builder().build();
    assertThat(s.getMaxSubsequentStateExecutions(TestWorkflow.State.begin), is(equalTo(100)));
  }

  @Test
  public void getMaxSubsequentStateExecutionsReturnsValueDefinedForTheState() {
    int executionsDefault = 200;
    int executionsForBegin = 300;
    WorkflowSettings s = new WorkflowSettings.Builder().setMaxSubsequentStateExecutions(executionsDefault)
        .setMaxSubsequentStateExecutions(TestWorkflow.State.begin, executionsForBegin).build();
    assertThat(s.getMaxSubsequentStateExecutions(TestWorkflow.State.begin), is(equalTo(executionsForBegin)));
  }

  @Test
  public void getMaxSubsequentStateExecutionsReturnsGivenDefaultValueWhenNotDefinedForState() {
    int executionsDefault = 200;
    WorkflowSettings s = new WorkflowSettings.Builder().setMaxSubsequentStateExecutions(executionsDefault).build();
    assertThat(s.getMaxSubsequentStateExecutions(TestWorkflow.State.begin), is(equalTo(executionsDefault)));
  }

  @Test
  public void deleteHistoryReturnsFalseRoughlyNineTimesOfTenByDefault() {
    WorkflowSettings.Builder b = new WorkflowSettings.Builder();
    b.rnd = mock(Random.class);
    WorkflowSettings s = b.build();

    when(b.rnd.nextInt(anyInt())).thenReturn(0);
    assertThat(s.deleteHistoryCondition.getAsBoolean(), is(true));

    IntStream.range(1, 10).forEach(i -> {
      when(b.rnd.nextInt(anyInt())).thenReturn(i);
      assertThat(s.deleteHistoryCondition.getAsBoolean(), is(false));
    });
  }

  @Test
  public void deleteHistoryConditionIsApplied() {
    WorkflowSettings s = new WorkflowSettings.Builder().setDeleteHistoryCondition(() -> true).build();

    assertThat(s.deleteHistoryCondition.getAsBoolean(), is(true));
  }

}
