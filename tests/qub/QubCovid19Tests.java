package qub;

public interface QubCovid19Tests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubCovid19.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null arguments", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19.main((String[])null),
                        new PreConditionFailure("arguments cannot be null."));
                });
            });

            runner.testGroup("getParameters(QubProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19.getParameters(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no command line arguments", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create())
                    {
                        final QubCovid19Parameters parameters = QubCovid19.getParameters(process);
                        test.assertNotNull(parameters);
                        test.assertSame(process.getOutputCharacterWriteStream(), parameters.getOutput());
                        test.assertNotNull(parameters.getDataSource());
                        test.assertInstanceOf(parameters.getDataSource(), Covid19GitDataSource.class);
                    }
                });

                runner.test("with \"--help\"", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("--help"))
                    {
                        final InMemoryCharacterStream output = new InMemoryCharacterStream();
                        process.setOutputCharacterWriteStream(output);

                        final QubCovid19Parameters parameters = QubCovid19.getParameters(process);
                        test.assertNull(parameters);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub/covid-19-java [--profiler] [--help]",
                                "  Used to gather, consolidate, and report data about the COVID-19 virus.",
                                "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                                "  --help(?): Show the help message for this application."
                            ),
                            Strings.getLines(output.getText().await()));
                        test.assertEqual(-1, process.getExitCode());
                    }
                });
            });

            runner.testGroup("run(QubCovid19Parameters)", () ->
            {
                runner.test("with null parameters", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19.run(null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                runner.test("with non-null parameters", (Test test) ->
                {
                    final InMemoryCharacterStream output = new InMemoryCharacterStream();
                    final Covid19InMemoryDataSource dataSource = Covid19InMemoryDataSource.create()
                        .setDailyReport(DateTime.create(2020, 3, 7),
                            Covid19DailyReport.create(
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("China")
                                    .setConfirmedCases(80770),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("Italy")
                                    .setConfirmedCases(5883),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("WA")
                                    .setConfirmedCases(102),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("MI")
                                    .setConfirmedCases(0)))
                        .setDailyReport(DateTime.create(2020, 3, 6),
                            Covid19DailyReport.create(
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("China")
                                    .setConfirmedCases(8077),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("Italy")
                                    .setConfirmedCases(588),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("WA")
                                    .setConfirmedCases(10),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("MI")
                                    .setConfirmedCases(0)))
                        .setDailyReport(DateTime.create(2020, 3, 4),
                            Covid19DailyReport.create(
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("China")
                                    .setConfirmedCases(807),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("Italy")
                                    .setConfirmedCases(58),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("WA")
                                    .setConfirmedCases(1),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("US")
                                    .setStateOrProvince("MI")
                                    .setConfirmedCases(0)))
                        .setDailyReport(DateTime.create(2020, 2, 29),
                            Covid19DailyReport.create(
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("China")
                                    .setConfirmedCases(80),
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("Italy")
                                    .setConfirmedCases(5)))
                        .setDailyReport(DateTime.create(2020, 2, 6),
                            Covid19DailyReport.create(
                                Covid19DailyReportDataRow.create()
                                    .setCountryOrRegion("China")
                                    .setConfirmedCases(8)));
                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, dataSource);

                    QubCovid19.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Refreshing data... Done.",
                            "",
                            "Summary:",
                            "Dates reported:     5       ",
                            "Countries reported: 3       ",
                            "Most recent report: 3/7/2020",
                            "",
                            "Confirmed Cases:",
                            "-----------------------------------------------------------------------------------",
                            "| Location        | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          | 86755    | 8675       | 866        | 85         | 8           |",
                            "| China           | 80770    | 8077       | 807        | 80         | 8           |",
                            "| Italy           | 5883     | 588        | 58         | 5          | 0           |",
                            "| South Korea     | 0        | 0          | 0          | 0          | 0           |",
                            "| USA             | 102      | 10         | 1          | 0          | 0           |",
                            "| Washington, USA | 0        | 0          | 0          | 0          | 0           |",
                            "| Michigan, USA   | 0        | 0          | 0          | 0          | 0           |",
                            "| New York, USA   | 0        | 0          | 0          | 0          | 0           |",
                            "| Florida, USA    | 0        | 0          | 0          | 0          | 0           |",
                            "| Utah, USA       | 0        | 0          | 0          | 0          | 0           |",
                            "-----------------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "------------------------------------------------------------------------",
                            "| Location        | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          | 78080      | 28629      | 12381      | 2891        |",
                            "| China           | 72693      | 26654      | 11527      | 2692        |",
                            "| Italy           | 5295       | 1941       | 839        | 196         |",
                            "| South Korea     | 0          | 0          | 0          | 0           |",
                            "| USA             | 92         | 33         | 14         | 3           |",
                            "| Washington, USA | 0          | 0          | 0          | 0           |",
                            "| Michigan, USA   | 0          | 0          | 0          | 0           |",
                            "| New York, USA   | 0          | 0          | 0          | 0           |",
                            "| Florida, USA    | 0          | 0          | 0          | 0           |",
                            "| Utah, USA       | 0          | 0          | 0          | 0           |",
                            "------------------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
