(function () {
  'use strict';

  var m = angular.module('nflowVisApp.services.executorPoller', [
    'nflowVisApp.services'
  ]);

  m.factory('ExecutorPoller', function ($log, $interval, config, Executors) {
    var started = false;

    var api = {};
    api.executors = [];
    api.start = start;
    return api;

    function start() {
      if (!started) {
        started = true;
        $log.info('Start executor poller with period ', config.radiator.pollPeriod, ' seconds');
        updateExecutors();
        $interval(updateExecutors, config.radiator.pollPeriod * 1000);
      }
      $log.info('Executor poller already started');
    }

    function updateExecutors() {
      $log.info('Fetching executors');
      Executors.query(function (executors) {
        angular.copy(executors, api.executors);
      });
    }
  });
})();