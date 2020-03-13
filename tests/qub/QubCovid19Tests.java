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
                            "Dates reported:     46      ",
                            "Countries reported: 96      ",
                            "Most recent report: 3/7/2020",
                            "",
                            "Confirmed Cases:",
                            "----------------------------------------------------------------------------",
                            "| Location | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   | 105821   | 101784     | 95120      | 86011      | 30817       |",
                            "| China    | 80770    | 80690      | 80386      | 79356      | 30587       |",
                            "| USA      | 402      | 262        | 149        | 68         | 11          |",
                            "| UK       | 207      | 164        | 86         | 23         | 2           |",
                            "----------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Change:",
                            "-----------------------------------------------------------------",
                            "| Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   | 26559      | 33223      | 42332      | 97526       |",
                            "| China    | 242        | 546        | 1576       | 50345       |",
                            "| USA      | 1401       | 1514       | 1595       | 1652        |",
                            "| UK       | 295        | 373        | 436        | 457         |",
                            "-----------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "-----------------------------------------------------------------",
                            "| Location | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global   | 26559      | 11074      | 6047       | 3250        |",
                            "| China    | 242        | 182        | 225        | 1678        |",
                            "| USA      | 1401       | 504        | 227        | 55          |",
                            "| UK       | 295        | 124        | 62         | 15          |",
                            "-----------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
