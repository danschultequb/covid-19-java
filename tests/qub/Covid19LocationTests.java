package qub;

public interface Covid19LocationTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Covid19Location.class, () ->
        {
            runner.testGroup("create(String)", () ->
            {
                final Action2<String,Throwable> createErrorTest = (String name, Throwable expected) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(name), (Test test) ->
                    {
                        test.assertThrows(() -> Covid19Location.create(name), expected);
                    });
                };

                createErrorTest.run(null, new PreConditionFailure("name cannot be null."));
                createErrorTest.run("", new PreConditionFailure("name cannot be empty."));

                final Action1<String> createTest = (String name) ->
                {
                    runner.test("with " + Strings.escapeAndQuote(name), (Test test) ->
                    {
                        final Covid19Location location = Covid19Location.create(name);
                        test.assertNotNull(location);
                        test.assertEqual(name, location.getName());
                        test.assertTrue(location.matches(Covid19DailyReportDataRow.create()));
                    });
                };

                createTest.run("a");
                createTest.run("ball");
            });

            runner.testGroup("setCondition(Covid19LocationCondition)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Covid19Location location = Covid19Location.create("hello");

                    test.assertThrows(() -> location.setCondition(null),
                        new PreConditionFailure("condition cannot be null."));

                    test.assertTrue(location.matches(Covid19DailyReportDataRow.create()));
                });

                runner.test("with non-null", (Test test) ->
                {
                    final Covid19Location location = Covid19Location.create("hello");

                    final Covid19Location setConditionResult = location.setCondition(Covid19LocationCondition.countryOrRegionEquals("USA"));
                    test.assertSame(location, setConditionResult);

                    test.assertFalse(location.matches(Covid19DailyReportDataRow.create()));
                    test.assertFalse(location.matches(Covid19DailyReportDataRow.create().setCountryOrRegion("France")));
                    test.assertTrue(location.matches(Covid19DailyReportDataRow.create().setCountryOrRegion("USA")));
                });
            });
        });
    }
}
