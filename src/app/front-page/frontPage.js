(function () {
  'use strict';

  var m = angular.module('nflowExplorer.frontPage', [
    'nflowExplorer.frontPage.definitionList',
    'nflowExplorer.services',
  ]);

  m.controller('FrontPageCtrl', function FrontPageCtrl(WorkflowDefinitions) {
    var self = this;
    self.definitions = WorkflowDefinitions.query();
  });

})();
