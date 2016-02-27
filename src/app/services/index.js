(function () {
  'use strict';

  angular.module('nflowExplorer.services', [
    'nflowExplorer.config',

    'nflowExplorer.services.ExecutorService',
    'nflowExplorer.services.GraphService',
    'nflowExplorer.services.Time',
    'nflowExplorer.services.WorkflowDefinitions',
    'nflowExplorer.services.WorkflowDefinitionStats',
    'nflowExplorer.services.Workflows',
    'nflowExplorer.services.WorkflowSearch',
    'nflowExplorer.services.WorkflowStatsPoller',
  ]);

})();

