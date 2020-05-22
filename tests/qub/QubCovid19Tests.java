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
                    test.assertThrows(() -> QubCovid19.main(null),
                        new PreConditionFailure("args cannot be null."));
                });
            });

            runner.testGroup("run(QubProcess)", () ->
            {
                runner.test("with null process", (Test test) ->
                {
                    test.assertThrows(() -> QubCovid19.run(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with " + Strings.escapeAndQuote("-?"), (Test test) ->
                {
                    try (QubProcess process = QubProcess.create("-?"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        QubCovid19.run(process);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-covid-19 [--action=]<action-name> [--help]",
                                "  Used to gather, consolidate, and report data about the COVID-19 virus.",
                                "  --action(a): The name of the action to invoke.",
                                "  --help(?): Show the help message for this application.",
                                "",
                                "Actions:",
                                "  config: Open the configuration file for this application.",
                                "  show (default): Report the current state of the COVID-19 virus in the configured locations."),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with unrecognized action (\"spam\")", (Test test) ->
                {
                    try (QubProcess process = QubProcess.create("spam"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        process.setOutputWriteStream(output);

                        QubCovid19.run(process);

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-covid-19 [--action=]<action-name> [--help]",
                                "  Used to gather, consolidate, and report data about the COVID-19 virus.",
                                "  --action(a): The name of the action to invoke.",
                                "  --help(?): Show the help message for this application.",
                                "",
                                "Actions:",
                                "  config: Open the configuration file for this application.",
                                "  show (default): Report the current state of the COVID-19 virus in the configured locations."),
                            Strings.getLines(output.getText().await()));
                    }
                });
            });
        });
    }
}
