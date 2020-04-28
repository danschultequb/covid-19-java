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
                        test.assertSame(process.getOutputWriteStream(), parameters.getOutput());
                        test.assertNotNull(parameters.getDataSource());
                        test.assertInstanceOf(parameters.getDataSource(), Covid19GitDataSource.class);
                    }
                });

                runner.test("with \"--help\"", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("--help"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

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
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
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
                            "Dates reported:            5",
                            "Countries reported:        3",
                            "Most recent report: 3/7/2020",
                            "",
                            "Confirmed Cases:",
                            "-----------------------------------------------------------------------------------",
                            "| Location        | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          |    86755 |       8675 |        866 |         85 |           8 |",
                            "| China           |    80770 |       8077 |        807 |         80 |           8 |",
                            "| Italy           |     5883 |        588 |         58 |          5 |           0 |",
                            "| South Korea     |        0 |          0 |          0 |          0 |           0 |",
                            "| USA             |      102 |         10 |          1 |          0 |           0 |",
                            "| Washington, USA |        0 |          0 |          0 |          0 |           0 |",
                            "| Michigan, USA   |        0 |          0 |          0 |          0 |           0 |",
                            "| New York, USA   |        0 |          0 |          0 |          0 |           0 |",
                            "| Florida, USA    |        0 |          0 |          0 |          0 |           0 |",
                            "| Utah, USA       |        0 |          0 |          0 |          0 |           0 |",
                            "-----------------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "------------------------------------------------------------------------",
                            "| Location        | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          |      78080 |      28629 |      12381 |        2891 |",
                            "| China           |      72693 |      26654 |      11527 |        2692 |",
                            "| Italy           |       5295 |       1941 |        839 |         196 |",
                            "| South Korea     |          0 |          0 |          0 |           0 |",
                            "| USA             |         92 |         33 |         14 |           3 |",
                            "| Washington, USA |          0 |          0 |          0 |           0 |",
                            "| Michigan, USA   |          0 |          0 |          0 |           0 |",
                            "| New York, USA   |          0 |          0 |          0 |           0 |",
                            "| Florida, USA    |          0 |          0 |          0 |           0 |",
                            "| Utah, USA       |          0 |          0 |          0 |           0 |",
                            "------------------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with actual data", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final Git git = Git.create(test.getProcess());
                    final Covid19GitDataSource dataSource = Covid19GitDataSource.create(test.getFileSystem().getFolder("C:/qub/qub/covid-19-java/data/").await(), git);
                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, dataSource);

                    QubCovid19.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Refreshing data... Done.",
                            "",
                            "Summary:",
                            "Dates reported:            97",
                            "Countries reported:       185",
                            "Most recent report: 4/27/2020",
                            "",
                            "Confirmed Cases:",
                            "------------------------------------------------------------------------------------",
                            "| Location        | 4/27/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          |   3041764 |    2971475 |    2810715 |    2472259 |      660693 |",
                            "| China           |     83918 |      83912 |      83899 |      83817 |       81999 |",
                            "| Italy           |    199414 |     197675 |     192994 |     181228 |       92472 |",
                            "| South Korea     |     10752 |      10738 |      10718 |      10674 |        9478 |",
                            "| USA             |    988197 |     965783 |     905333 |     784326 |      121465 |",
                            "| Washington, USA |     13686 |      13521 |      12977 |      12114 |        4030 |",
                            "| Michigan, USA   |     38210 |      37778 |      36641 |      32000 |        4650 |",
                            "| New York, USA   |    291996 |     288045 |     271590 |     253060 |       52410 |",
                            "| Florida, USA    |     32138 |      31532 |      30533 |      27059 |        3763 |",
                            "| Utah, USA       |      4236 |       4123 |       3782 |       3213 |         602 |",
                            "------------------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "------------------------------------------------------------------------",
                            "| Location        | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          |      70289 |      77016 |      81357 |       79369 |",
                            "| China           |          6 |          6 |         14 |          63 |",
                            "| Italy           |       1739 |       2140 |       2598 |        3564 |",
                            "| South Korea     |         14 |         11 |         11 |          42 |",
                            "| USA             |      22414 |      27621 |      29124 |       28891 |",
                            "| Washington, USA |        165 |        236 |        224 |         321 |",
                            "| Michigan, USA   |        432 |        523 |        887 |        1118 |",
                            "| New York, USA   |       3951 |       6802 |       5562 |        7986 |",
                            "| Florida, USA    |        606 |        535 |        725 |         945 |",
                            "| Utah, USA       |        113 |        151 |        146 |         121 |",
                            "------------------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
