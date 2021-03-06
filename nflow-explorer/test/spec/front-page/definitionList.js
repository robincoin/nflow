'use strict';

describe('Directive: definitionList', function () {

  beforeEach(module('nflowExplorer.frontPage.definitionList'));
  beforeEach(module('nflowExplorer.karma.templates'));

  it('sets definitions into view model', inject(function ($rootScope, $compile) {

    var elem = $compile('<definition-list definitions="expected"></definition-list>')($rootScope);
    $rootScope.$apply(function() { $rootScope.expected = ['foo', 'bar']; });

    var ctrl = elem.isolateScope().ctrl;
    expect(ctrl.definitions).toEqual(['foo', 'bar']);
  }));
});
