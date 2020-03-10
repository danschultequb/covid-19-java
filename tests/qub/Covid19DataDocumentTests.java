package qub;

public interface Covid19DataDocumentTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Covid19DataDocument.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                test.assertNotNull(dataDocument);
                test.assertEqual(Iterable.create(), dataDocument.getDatesReported());
                test.assertEqual(Iterable.create(), dataDocument.getCountriesReported());
                test.assertEqual(0, dataDocument.getConfirmedCases());
            });

            runner.testGroup("parse(ByteReadStream)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse((ByteReadStream)null),
                        new PreConditionFailure("byteReadStream cannot be null."));
                });
            });

            runner.testGroup("parse(String)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse((String)null),
                        new PreConditionFailure("characters cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse("").await(),
                        new ParseException("A COVID-19 data document must contain a header row that starts with [Province/State,Country/Region,Lat,Long]."));
                });

                runner.test("with not all required headers", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse("Province/State,Country/Region,Lat").await(),
                        new ParseException("The first row of the COVID-19 data document must start with [Province/State,Country/Region,Lat,Long]."));
                });

                runner.test("with only required headers", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        "Province/State,Country/Region,Lat,Long")
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(Iterable.create(), dataDocument.getDatesReported());
                });

                runner.test("with dates reported", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20,1/24/20,1/25/20,1/26/20")
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 22),
                            DateTime.create(2020, 1, 23),
                            DateTime.create(2020, 1, 24),
                            DateTime.create(2020, 1, 25),
                            DateTime.create(2020, 1, 26)),
                        dataDocument.getDatesReported());
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported());
                    test.assertEqual(0, dataDocument.getConfirmedCases());
                });

                runner.test("with one data row", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                        "Anhui,Mainland China,31.8257,117.2264,1,9,15,39,60,70,106,152\n")
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 22),
                            DateTime.create(2020, 1, 23)),
                        dataDocument.getDatesReported());
                    test.assertEqual(
                        Iterable.create(
                            "Mainland China"),
                        dataDocument.getCountriesReported());
                    test.assertEqual(9, dataDocument.getConfirmedCases());
                });

                runner.test("with two data rows for the same country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                        "Anhui,Mainland China,31.8257,117.2264,1,9,15,39,60,70,106,152\n" +
                        "Chongqing,Mainland China,30.0572,107.874,6,9,27,57,75,110,132")
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 22),
                            DateTime.create(2020, 1, 23)),
                        dataDocument.getDatesReported());
                    test.assertEqual(
                        Iterable.create(
                            "Mainland China"),
                        dataDocument.getCountriesReported());
                    test.assertEqual(18, dataDocument.getConfirmedCases());
                });

                runner.test("with two data rows for different countries", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        "Province/State,Country/Region,Lat,Long,1/22/20,1/23/20\n" +
                        "Anhui,Mainland China,31.8257,117.2264,1,9,15,39,60,70,106,152\n" +
                        "\"Sacramento County, CA\",US,38.4747,-121.3542,0,0,0,0")
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 22),
                            DateTime.create(2020, 1, 23)),
                        dataDocument.getDatesReported());
                    test.assertEqual(
                        Iterable.create(
                            "Mainland China",
                            "US"),
                        dataDocument.getCountriesReported());
                    test.assertEqual(9, dataDocument.getConfirmedCases());
                });
            });

            runner.testGroup("parse(Iterator<Character>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse((Iterator<Character>)null),
                        new PreConditionFailure("characters cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse(Strings.iterate("")).await(),
                        new ParseException("A COVID-19 data document must contain a header row that starts with [Province/State,Country/Region,Lat,Long]."));
                });
            });

            runner.testGroup("parse(CSVDocument)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse((CSVDocument)null),
                        new PreConditionFailure("csvDocument cannot be null."));
                });
            });
        });
    }
}
