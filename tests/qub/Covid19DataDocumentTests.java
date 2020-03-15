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

                runner.test("with empty", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse(new InMemoryByteStream().endOfStream()).await(),
                        new ParseException("A COVID-19 data document must contain a header row that starts with [Province/State,Country/Region,Lat,Long]."));
                });

                runner.test("with not all required headers", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DataDocument.parse(new InMemoryByteStream("Province/State,Country/Region,Lat".getBytes()).endOfStream()).await(),
                        new ParseException("The first row of the COVID-19 data document must start with [Province/State,Country/Region,Lat,Long]."));
                });

                runner.test("with only required headers", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        new InMemoryByteStream("Province/State,Country/Region,Lat,Long".getBytes()).endOfStream())
                        .await();
                    test.assertNotNull(dataDocument);
                    test.assertEqual(Iterable.create(), dataDocument.getDatesReported());
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

            runner.testGroup("getDatesReported()", () ->
            {
                runner.test("with no dates reported", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(Iterable.create(), dataDocument.getDatesReported());
                });

                runner.test("with one date reported", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2)),
                        dataDocument.getDatesReported());
                });

                runner.test("with multiple dates reported", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20", "1/3/20", "1/4/20", "1/5/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2),
                            DateTime.create(2020, 1, 3),
                            DateTime.create(2020, 1, 4),
                            DateTime.create(2020, 1, 5)),
                        dataDocument.getDatesReported());
                });
            });

            runner.testGroup("getDatesReported(DateTime)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertThrows(() -> dataDocument.getDatesReported(null),
                        new PreConditionFailure("asOf cannot be null."));
                });

                runner.test("with no dates reported", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(Iterable.create(), dataDocument.getDatesReported(DateTime.create(2020, 3, 1)));
                });

                runner.test("with one date reported and asOf before reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with one date reported and asOf equal to reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2)),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 2)));
                });

                runner.test("with one date reported and asOf after reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2)),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 3)));
                });

                runner.test("with multiple dates reported and asOf before first reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20", "1/3/20", "1/4/20", "1/5/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with multiple dates reported and asOf within reported dates", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20", "1/3/20", "1/4/20", "1/5/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2),
                            DateTime.create(2020, 1, 3)),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 3)));
                });

                runner.test("with multiple dates reported and asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(
                                List.create(Covid19DataDocument.requiredHeaders)
                                    .addAll("1/2/20", "1/3/20", "1/4/20", "1/5/20"))))
                        .await();
                    test.assertEqual(
                        Iterable.create(
                            DateTime.create(2020, 1, 2),
                            DateTime.create(2020, 1, 3),
                            DateTime.create(2020, 1, 4),
                            DateTime.create(2020, 1, 5)),
                        dataDocument.getDatesReported(DateTime.create(2020, 1, 6)));
                });
            });

            runner.testGroup("getCountriesReported()", () ->
            {
                runner.test("with no data rows", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported());
                });

                runner.test("with one data row with null country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", null, "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported());
                });

                runner.test("with one data row with empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported());
                });

                runner.test("with one data row with non-empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country"), dataDocument.getCountriesReported());
                });

                runner.test("with multiple data rows with same country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country"), dataDocument.getCountriesReported());
                });

                runner.test("with multiple data rows with different country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country-1", "fake-country-2"), dataDocument.getCountriesReported());
                });
            });

            runner.testGroup("getCountriesReported(DateTime)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertThrows(() -> dataDocument.getCountriesReported(null),
                        new PreConditionFailure("asOf cannot be null."));
                });

                runner.test("with no data rows", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with one data row with null country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", null, "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with one data row with empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with one data row with non-empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country"), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with multiple data rows with same country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country"), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with multiple data rows with different country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country-1", "fake-country-2"), dataDocument.getCountriesReported(DateTime.create(2020, 1, 1)));
                });

                runner.test("with multiple data rows with different country with asOf before first reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2019, 1, 1)));
                });

                runner.test("with multiple data rows with different country with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(Iterable.create("fake-country-1", "fake-country-2"), dataDocument.getCountriesReported(DateTime.create(2021, 1, 1)));
                });

                runner.test("with multiple data rows with 0 reported cases with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "0"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "0")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2020, 1, 3)));
                });

                runner.test("with multiple data rows with null reported cases with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", null, "0"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", null)))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2021, 1, 3)));
                });

                runner.test("with multiple data rows with empty reported cases with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "", "0"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2021, 1, 3)));
                });

                runner.test("with multiple data rows with non-numeric reported cases with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "abc", "0"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "def")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2021, 1, 3)));
                });

                runner.test("with multiple data rows with non-integer reported cases with asOf after last reported date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1.2", "0"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "3.4")))
                        .await();
                    test.assertEqual(Iterable.create(), dataDocument.getCountriesReported(DateTime.create(2021, 1, 3)));
                });
            });

            runner.testGroup("getConfirmedCases()", () ->
            {
                runner.test("with no data rows", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(0, dataDocument.getConfirmedCases());
                });

                runner.test("with one data row with null country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", null, "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(2, dataDocument.getConfirmedCases());
                });

                runner.test("with one data row with empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(2, dataDocument.getConfirmedCases());
                });

                runner.test("with one data row with non-empty country cell", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "1", "2")))
                        .await();
                    test.assertEqual(2, dataDocument.getConfirmedCases());
                });

                runner.test("with multiple data rows with same country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(6, dataDocument.getConfirmedCases());
                });

                runner.test("with multiple data rows with different country", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "1", "2"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "3", "4")))
                        .await();
                    test.assertEqual(6, dataDocument.getConfirmedCases());
                });
            });

            runner.testGroup("getConfirmedCases(DateTime,Function1<CSVRow,Boolean>)", () ->
            {
                runner.test("with null date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertThrows(() -> dataDocument.getConfirmedCases(null, (CSVRow row) -> true),
                        new PreConditionFailure("date cannot be null."));
                });

                runner.test("with null rowCondition", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertThrows(() -> dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), null),
                        new PreConditionFailure("rowCondition cannot be null."));
                });

                runner.test("with no data rows", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.create();
                    test.assertEqual(0, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });

                runner.test("with one data row with cases after date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "0", "1")))
                        .await();
                    test.assertEqual(0, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 1), (CSVRow row) -> true));
                });

                runner.test("with one data row with cases before date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "0", "1")))
                        .await();
                    test.assertEqual(1, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });

                runner.test("with one data row with cases before date but doesn't match condition", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region", "fake-country", "fake-latitude", "fake-longitude", "0", "1")))
                        .await();
                    test.assertEqual(0, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> false));
                });

                runner.test("with multiple data rows with cases after date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country", "fake-latitude", "fake-longitude", "0", "1"),
                            CSVRow.create("fake-region-2", "fake-country", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(0, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 1), (CSVRow row) -> true));
                });

                runner.test("with multiple data rows with cases before date", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "1"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(3, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });

                runner.test("with multiple data rows with cases before date but don't match the condition", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "1"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(0, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> false));
                });

                runner.test("with multiple data rows with cases before date but one doesn't match the condition", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "1"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(
                        2,
                        dataDocument.getConfirmedCases(
                            DateTime.create(2020, 1, 2),
                            (CSVRow row) -> row.getCell(0).equals("fake-region-2")));
                });

                runner.test("with multiple data rows with cases before date but confirmed cases is null", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", null),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(2, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });

                runner.test("with multiple data rows with cases before date but confirmed cases is empty", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "1"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "")))
                        .await();
                    test.assertEqual(1, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });

                runner.test("with multiple data rows with cases before date but confirmed cases is non-numeric", (Test test) ->
                {
                    final Covid19DataDocument dataDocument = Covid19DataDocument.parse(
                        CSVDocument.create(
                            CSVRow.create(List.create(Covid19DataDocument.requiredHeaders).addAll("1/1/20", "1/2/20")),
                            CSVRow.create("fake-region-1", "fake-country-1", "fake-latitude", "fake-longitude", "0", "apples"),
                            CSVRow.create("fake-region-2", "fake-country-2", "fake-latitude", "fake-longitude", "0", "2")))
                        .await();
                    test.assertEqual(2, dataDocument.getConfirmedCases(DateTime.create(2020, 1, 2), (CSVRow row) -> true));
                });
            });
        });
    }
}
