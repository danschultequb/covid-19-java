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
                        test.assertNotNull(parameters.getHttpClient());
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
                    final HttpClient httpClient = HttpClient.create(test.getNetwork());
                    final DateTime now = DateTime.create(2020, 3, 7);
                    final QubCovid19Parameters parameters = new QubCovid19Parameters(output, httpClient, now);

                    QubCovid19.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Getting confirmed cases data... Done.",
                            "Parsing confirmed cases data... Done.",
                            "",
                            "Summary:",
                            "Dates reported:     48      ",
                            "Countries reported: 111     ",
                            "Most recent report: 3/9/2020",
                            "",
                            "Confirmed Cases:",
                            "------------------------------------------------------------------------",
                            "Location | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago",
                            "Global   | 113583   | 101799     | 95123      | 86013      | 30818      ",
                            "China    | 80735    | 80573      | 80271      | 79251      | 30553      ",
                            "USA      | 605      | 277        | 152        | 70         | 12         ",
                            "UK       | 321      | 163        | 85         | 23         | 2          ",
                            "------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Change:",
                            "-------------------------------------------------------------",
                            "Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago",
                            "Global   | 11784      | 18460      | 27570      | 82765      ",
                            "China    | 162        | 464        | 1484       | 50182      ",
                            "USA      | 328        | 453        | 535        | 593        ",
                            "UK       | 158        | 236        | 298        | 319        ",
                            "-------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "-------------------------------------------------------------",
                            "Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago",
                            "Global   | 11784      | 6153       | 3938       | 2758       ",
                            "China    | 162        | 154        | 212        | 1672       ",
                            "USA      | 328        | 151        | 76         | 19         ",
                            "UK       | 158        | 78         | 42         | 10         ",
                            "-------------------------------------------------------------"
                        ),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
