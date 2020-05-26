package qub;

public interface QubCovid19ShowTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubCovid19.class, () ->
        {
            runner.testGroup("getParameters(QubProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19Show.getParameters(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with no command line arguments", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create())
                    {
                        final QubCovid19ShowParameters parameters = QubCovid19Show.getParameters(process);
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

                        final QubCovid19ShowParameters parameters = QubCovid19Show.getParameters(process);
                        test.assertNull(parameters);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-covid-19 show [--profiler] [--help] [--verbose]",
                                "  Report the current state of the COVID-19 virus in the configured locations.",
                                "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                                "  --help(?): Show the help message for this application.",
                                "  --verbose(v): Whether or not to show verbose logs."
                            ),
                            Strings.getLines(output.getText().await()));
                        test.assertEqual(-1, process.getExitCode());
                    }
                });
            });

            runner.testGroup("run(QubCovid19ShowParameters)", () ->
            {
                runner.test("with null parameters", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19Show.run(null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                runner.test("with non-null parameters", (Test test) ->
                {
                    final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    dataFolder.setFileContentsAsString("configuration.json",
                        Covid19Configuration.create()
                            .addLocation(Covid19Location.create("Global"))
                            .addLocation(Covid19Location.create("USA")
                                .setCondition(Covid19LocationCondition.countryOrRegionEquals("US")))
                            .toString()).await();
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
                    final QubCovid19ShowParameters parameters = new QubCovid19ShowParameters(output, verbose, dataFolder, dataSource);

                    QubCovid19Show.run(parameters);

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
                            "----------------------------------------------------------------------------",
                            "| Location | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   |    86755 |       8675 |        866 |         85 |           8 |",
                            "| USA      |      102 |         10 |          1 |          0 |           0 |",
                            "----------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "-----------------------------------------------------------------",
                            "| Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   |      78080 |      28629 |      12381 |        2891 |",
                            "| USA      |         92 |         33 |         14 |           3 |",
                            "-----------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));

                    test.assertEqual(
                        JSONObject.create()
                            .setArray("locations", Iterable.create(
                                JSONObject.create()
                                    .setString("name", "Global"),
                                JSONObject.create()
                                    .setString("name", "USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("propertyName", "countryOrRegion")
                                        .setString("operator", "Equals")
                                        .setString("expectedPropertyValue", "US"))
                            )),
                        JSON.parseObject(dataFolder.getFile("configuration.json").await().getContentsAsString().await()).await());
                });

                runner.test("with actual data", runner.skip(), (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    dataFolder.setFileContentsAsString("configuration.json",
                        Covid19Configuration.create()
                            .addLocation(Covid19Location.create("Global"))
                            .addLocation(Covid19Location.create("USA")
                                .setCondition(Covid19LocationCondition.countryOrRegionEquals("US")))
                            .toString()).await();
                    final Git git = Git.create(test.getProcess());
                    final Covid19GitDataSource dataSource = Covid19GitDataSource.create(test.getFileSystem().getFolder("C:/qub/qub/covid-19-java/data/").await(), git, verbose);
                    final QubCovid19ShowParameters parameters = new QubCovid19ShowParameters(output, verbose, dataFolder, dataSource);

                    QubCovid19Show.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Refreshing data... Done.",
                            "",
                            "Summary:",
                            "Dates reported:           121",
                            "Countries reported:       188",
                            "Most recent report: 5/21/2020",
                            "",
                            "Confirmed Cases:",
                            "-----------------------------------------------------------------------------",
                            "| Location | 5/21/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   |   5102424 |    4996472 |    4801943 |    4442163 |     2549123 |",
                            "| USA      |   1577147 |    1551853 |    1508308 |    1417774 |      811865 |",
                            "-----------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "-----------------------------------------------------------------",
                            "| Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   |     105952 |     100160 |      94323 |       85110 |",
                            "| USA      |      25294 |      22946 |      22767 |       25509 |",
                            "-----------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));

                    test.assertEqual(
                        JSONObject.create()
                            .setArray("locations", Iterable.create(
                                JSONObject.create()
                                    .setString("name", "Global"),
                                JSONObject.create()
                                    .setString("name", "USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("propertyName", "countryOrRegion")
                                        .setString("operator", "Equals")
                                        .setString("expectedPropertyValue", "US"))
                            )),
                        JSON.parseObject(dataFolder.getFile("configuration.json").await().getContentsAsString().await()).await());
                });
            });
        });
    }
}
