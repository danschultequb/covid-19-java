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
                            "Countries reported: 95      ",
                            "Most recent report: 3/7/2020",
                            "",
                            "Confirmed Cases:",
                            "-----------------------------------------------------------------------------------",
                            "| Location        | 3/7/2020 | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          | 105821   | 101784     | 95120      | 86011      | 30817       |",
                            "| China           | 80770    | 80690      | 80386      | 79356      | 30587       |",
                            "| Italy           | 5883     | 4636       | 3089       | 1128       | 2           |",
                            "| South Korea     | 7041     | 6593       | 5621       | 3150       | 23          |",
                            "| USA             | 402      | 262        | 149        | 68         | 11          |",
                            "| Washington, USA | 102      | 78         | 39         | 7          | 1           |",
                            "| Michigan, USA   | 0        | 0          | 0          | 0          | 0           |",
                            "| New York, USA   | 76       | 31         | 11         | 0          | 0           |",
                            "| Florida, USA    | 7        | 3          | 2          | 0          | 0           |",
                            "| Utah, USA       | 1        | 0          | 0          | 0          | 0           |",
                            "-----------------------------------------------------------------------------------",
                            "",
                            "Confirmed Cases Average Change Per Day:",
                            "------------------------------------------------------------------------",
                            "| Location        | 1 days ago | 3 days ago | 7 days ago | 30 days ago |",
                            "| Global          | 4037       | 3567       | 2830       | 2500        |",
                            "| China           | 80         | 128        | 202        | 1672        |",
                            "| Italy           | 1247       | 931        | 679        | 196         |",
                            "| South Korea     | 448        | 473        | 555        | 233         |",
                            "| USA             | 140        | 84         | 47         | 13          |",
                            "| Washington, USA | 24         | 21         | 13         | 3           |",
                            "| Michigan, USA   | 0          | 0          | 0          | 0           |",
                            "| New York, USA   | 45         | 21         | 10         | 2           |",
                            "| Florida, USA    | 4          | 1          | 1          | 0           |",
                            "| Utah, USA       | 1          | 0          | 0          | 0           |",
                            "------------------------------------------------------------------------"),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }
}
