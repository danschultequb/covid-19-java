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
                                "Usage: qub/covid-19-java [--profiler] [--help] [--verbose]",
                                "  Used to gather, consolidate, and report data about the COVID-19 virus.",
                                "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                                "  --help(?): Show the help message for this application.",
                                "  --verbose(v): Whether or not to show verbose logs."
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
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
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
                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, verbose, dataFolder, dataSource);

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
                            "----------------------------------------------------------------------------------------",
                            "| Location             | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global               |    86755 |       8675 |        866 |         85 |           8 |",
                            "| China                |    80770 |       8077 |        807 |         80 |           8 |",
                            "| Italy                |     5883 |        588 |         58 |          5 |           0 |",
                            "| South Korea          |        0 |          0 |          0 |          0 |           0 |",
                            "| USA                  |      102 |         10 |          1 |          0 |           0 |",
                            "| Washington, USA      |        0 |          0 |          0 |          0 |           0 |",
                            "| Michigan, USA        |        0 |          0 |          0 |          0 |           0 |",
                            "| New York, USA        |        0 |          0 |          0 |          0 |           0 |",
                            "| Florida, USA         |        0 |          0 |          0 |          0 |           0 |",
                            "| Utah, USA            |        0 |          0 |          0 |          0 |           0 |",
                            "| Utah County, UT, USA |        0 |          0 |          0 |          0 |           0 |",
                            "----------------------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "-----------------------------------------------------------------------------",
                            "| Location             | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global               |      78080 |      28629 |      12381 |        2891 |",
                            "| China                |      72693 |      26654 |      11527 |        2692 |",
                            "| Italy                |       5295 |       1941 |        839 |         196 |",
                            "| South Korea          |          0 |          0 |          0 |           0 |",
                            "| USA                  |         92 |         33 |         14 |           3 |",
                            "| Washington, USA      |          0 |          0 |          0 |           0 |",
                            "| Michigan, USA        |          0 |          0 |          0 |           0 |",
                            "| New York, USA        |          0 |          0 |          0 |           0 |",
                            "| Florida, USA         |          0 |          0 |          0 |           0 |",
                            "| Utah, USA            |          0 |          0 |          0 |           0 |",
                            "| Utah County, UT, USA |          0 |          0 |          0 |           0 |",
                            "-----------------------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));

                    test.assertEqual(
                        JSONObject.create()
                            .setArray("locations", Iterable.create(
                                JSONObject.create()
                                    .setString("name", "Global"),
                                JSONObject.create()
                                    .setString("name", "China")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "Or")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "China"),
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "Mainland China")))),
                                JSONObject.create()
                                    .setString("name", "Italy")
                                    .setObject("condition", JSONObject.create()
                                        .setString("propertyName", "countryOrRegion")
                                        .setString("operator", "Equals")
                                        .setString("expectedPropertyValue", "Italy")),
                                JSONObject.create()
                                    .setString("name", "South Korea")
                                    .setObject("condition", JSONObject.create()
                                        .setString("propertyName", "countryOrRegion")
                                        .setString("operator", "Equals")
                                        .setString("expectedPropertyValue", "Korea, South")),
                                JSONObject.create()
                                    .setString("name", "USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("propertyName", "countryOrRegion")
                                        .setString("operator", "Equals")
                                        .setString("expectedPropertyValue", "US")),
                                JSONObject.create()
                                    .setString("name", "Washington, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "Washington"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", WA")
                                                ))
                                        ))
                                    ),
                                JSONObject.create()
                                    .setString("name", "Michigan, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "Michigan"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", MI")
                                                ))
                                        ))
                                    ),
                                JSONObject.create()
                                    .setString("name", "New York, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "New York"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", NY")
                                                ))
                                        ))
                                    ),
                                JSONObject.create()
                                    .setString("name", "Florida, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "Florida"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", FL")
                                                ))
                                        ))
                                    ),
                                JSONObject.create()
                                    .setString("name", "Utah, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "Utah"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", UT")
                                                ))
                                        ))
                                    ),
                                JSONObject.create()
                                    .setString("name", "Utah County, UT, USA")
                                    .setObject("condition", JSONObject.create()
                                        .setString("operator", "And")
                                        .setArray("conditions", Iterable.create(
                                            JSONObject.create()
                                                .setString("propertyName", "countryOrRegion")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "US"),
                                            JSONObject.create()
                                                .setString("operator", "Or")
                                                .setArray("conditions", Iterable.create(
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Equals")
                                                        .setString("expectedPropertyValue", "Utah"),
                                                    JSONObject.create()
                                                        .setString("propertyName", "stateOrProvince")
                                                        .setString("operator", "Contains")
                                                        .setString("expectedPropertyValue", ", UT")
                                                )),
                                            JSONObject.create()
                                                .setString("propertyName", "county")
                                                .setString("operator", "Equals")
                                                .setString("expectedPropertyValue", "Utah")
                                        ))
                                    )
                            )),
                        JSON.parseObject(dataFolder.getFile("configuration.json").await().getContentsAsString().await()).await());
                });

                runner.test("with actual data", runner.skip(false), (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final VerboseCharacterWriteStream verbose = new VerboseCharacterWriteStream(false, output);
                    final InMemoryFileSystem fileSystem = new InMemoryFileSystem(test.getClock());
                    fileSystem.createRoot("/").await();
                    final Folder dataFolder = fileSystem.getFolder("/data/").await();
                    dataFolder.setFileContentsAsString("configuration.json",
                        QubCovid19Configuration.create()
                            .addLocation(Covid19Location.create("Global"))
                            .addLocation(Covid19Location.create("USA")
                                .setCondition(Covid19LocationCondition.countryOrRegionEquals("US")))
                            .toString()).await();
                    final Git git = Git.create(test.getProcess());
                    final Covid19GitDataSource dataSource = Covid19GitDataSource.create(test.getFileSystem().getFolder("C:/qub/qub/covid-19-java/data/").await(), git, verbose);
                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, verbose, dataFolder, dataSource);

                    QubCovid19.run(parameters);

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
