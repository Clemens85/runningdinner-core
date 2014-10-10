package org.runningdinner.core.dinnerplan;

import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.RunningDinnerConfig;

public interface DinnerPlanGenerator {

	public void generateDinnerExecutionPlan(final GeneratedTeamsResult generatedTeams, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException;
}
