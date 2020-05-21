package qub;

public interface Covid19LocationConditionTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Covid19LocationCondition.class, () ->
        {
            runner.testGroup("and(Covid19LocationCondition...)", () ->
            {
                runner.test("with no arguments", (Test test) ->
                {
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.and();
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.And, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(), groupCondition.getConditions().await());
                });

                runner.test("with one condition", (Test test) ->
                {
                    final Covid19LocationCondition condition = Covid19LocationCondition.countyEquals("spam");
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.and(condition);
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.And, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(condition), groupCondition.getConditions().await());
                });

                runner.test("with two conditions", (Test test) ->
                {
                    final Covid19LocationCondition condition1 = Covid19LocationCondition.countyEquals("spam");
                    final Covid19LocationCondition condition2 = Covid19LocationCondition.countryOrRegionContains("stuff");
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.and(condition1, condition2);
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.And, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(condition1, condition2), groupCondition.getConditions().await());
                });

                runner.test("with null array", (Test test) ->
                {
                    test.assertThrows(() -> Covid19LocationCondition.and((Covid19LocationCondition[])null),
                        new PreConditionFailure("conditions cannot be null."));
                });

                runner.test("with null condition", (Test test) ->
                {
                    test.assertThrows(() -> Covid19LocationCondition.and((Covid19LocationCondition)null),
                        new PreConditionFailure("condition cannot be null."));
                });
            });

            runner.testGroup("or(Covid19LocationCondition...)", () ->
            {
                runner.test("with no arguments", (Test test) ->
                {
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.or();
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.Or, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(), groupCondition.getConditions().await());
                });

                runner.test("with one condition", (Test test) ->
                {
                    final Covid19LocationCondition condition = Covid19LocationCondition.countyEquals("spam");
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.or(condition);
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.Or, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(condition), groupCondition.getConditions().await());
                });

                runner.test("with two conditions", (Test test) ->
                {
                    final Covid19LocationCondition condition1 = Covid19LocationCondition.countyEquals("spam");
                    final Covid19LocationCondition condition2 = Covid19LocationCondition.countryOrRegionContains("stuff");
                    final Covid19LocationGroupCondition groupCondition = Covid19LocationCondition.or(condition1, condition2);
                    test.assertNotNull(groupCondition);
                    test.assertEqual(Covid19LocationGroupConditionOperator.Or, groupCondition.getOperator().await());
                    test.assertEqual(Iterable.create(condition1, condition2), groupCondition.getConditions().await());
                });

                runner.test("with null array", (Test test) ->
                {
                    test.assertThrows(() -> Covid19LocationCondition.or((Covid19LocationCondition[])null),
                        new PreConditionFailure("conditions cannot be null."));
                });

                runner.test("with null condition", (Test test) ->
                {
                    test.assertThrows(() -> Covid19LocationCondition.or((Covid19LocationCondition)null),
                        new PreConditionFailure("condition cannot be null."));
                });
            });

            runner.testGroup("countryOrRegionEquals(String)", () ->
            {
                final Action1<String> countryOrRegionEqualsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.countryOrRegionEquals(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.countryOrRegionPropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Equals, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                countryOrRegionEqualsTest.run(null);
                countryOrRegionEqualsTest.run("");
                countryOrRegionEqualsTest.run("China");
            });

            runner.testGroup("countryOrRegionContains(String)", () ->
            {
                final Action1<String> countryOrRegionContainsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.countryOrRegionContains(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.countryOrRegionPropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Contains, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                countryOrRegionContainsTest.run(null);
                countryOrRegionContainsTest.run("");
                countryOrRegionContainsTest.run("Chi");
            });

            runner.testGroup("stateOrProvinceEquals(String)", () ->
            {
                final Action1<String> stateOrProvinceEqualsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.stateOrProvinceEquals(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.stateOrProvincePropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Equals, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                stateOrProvinceEqualsTest.run(null);
                stateOrProvinceEqualsTest.run("");
                stateOrProvinceEqualsTest.run("washington");
            });

            runner.testGroup("stateOrProvinceContains(String)", () ->
            {
                final Action1<String> stateOrProvinceContainsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.stateOrProvinceContains(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.stateOrProvincePropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Contains, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                stateOrProvinceContainsTest.run(null);
                stateOrProvinceContainsTest.run("");
                stateOrProvinceContainsTest.run("hingt");
            });

            runner.testGroup("countyEquals(String)", () ->
            {
                final Action1<String> countyEqualsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.countyEquals(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.countyPropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Equals, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                countyEqualsTest.run(null);
                countyEqualsTest.run("");
                countyEqualsTest.run("washington");
            });

            runner.testGroup("countyContains(String)", () ->
            {
                final Action1<String> countyContainsTest = (String expectedPropertyValue) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(expectedPropertyValue), (Test test) ->
                    {
                        final Covid19LocationPropertyCondition propertyCondition = Covid19LocationCondition.countyContains(expectedPropertyValue);
                        test.assertNotNull(propertyCondition);
                        test.assertEqual(Covid19DailyReportDataRow.countyPropertyName, propertyCondition.getPropertyName().await());
                        test.assertEqual(Covid19LocationPropertyConditionOperator.Contains, propertyCondition.getOperator().await());
                        test.assertEqual(expectedPropertyValue, propertyCondition.getExpectedPropertyValue().await());
                    });
                };

                countyContainsTest.run(null);
                countyContainsTest.run("");
                countyContainsTest.run("hingt");
            });
        });
    }
}
