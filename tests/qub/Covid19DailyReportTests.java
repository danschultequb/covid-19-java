package qub;

public interface Covid19DailyReportTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Covid19DailyReport.class, () ->
        {
            runner.testGroup("create(Iterable<Covid19DailyReportDataRow>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReport.create((Iterable<Covid19DailyReportDataRow>)null),
                        new PreConditionFailure("dataRows cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(Iterable.create());
                    test.assertNotNull(dailyReport);
                    test.assertEqual(Iterable.create(), dailyReport.getDataRows());
                });

                runner.test("with non-empty", (Test test) ->
                {
                    final Iterable<Covid19DailyReportDataRow> dataRows = Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(5));
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(dataRows);
                    test.assertNotNull(dailyReport);
                    test.assertEqual(dataRows, dailyReport.getDataRows());
                });
            });

            runner.testGroup("create(Covid19DailyReportDataRow...)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReport.create((Covid19DailyReportDataRow[])null),
                        new PreConditionFailure("dataRows cannot be null."));
                });

                runner.test("with no arguments", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create();
                    test.assertNotNull(dailyReport);
                    test.assertEqual(Iterable.create(), dailyReport.getDataRows());
                });

                runner.test("with one argument", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(
                        Covid19DailyReportDataRow.create()
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(200));
                    test.assertNotNull(dailyReport);
                    test.assertEqual(
                        Iterable.create(
                            Covid19DailyReportDataRow.create()
                                .setCountryOrRegion("fake-country-2")
                                .setConfirmedCases(200)),
                        dailyReport.getDataRows());
                });

                runner.test("with multiple arguments", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(
                        Covid19DailyReportDataRow.create()
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCountryOrRegion("fake-country-3")
                            .setConfirmedCases(300));
                    test.assertNotNull(dailyReport);
                    test.assertEqual(
                        Iterable.create(
                            Covid19DailyReportDataRow.create()
                                .setCountryOrRegion("fake-country-2")
                                .setConfirmedCases(200),
                            Covid19DailyReportDataRow.create()
                                .setCountryOrRegion("fake-country-3")
                                .setConfirmedCases(300)),
                        dailyReport.getDataRows());
                });

                runner.test("with empty array", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(new Covid19DailyReportDataRow[0]);
                    test.assertNotNull(dailyReport);
                    test.assertEqual(Iterable.create(), dailyReport.getDataRows());
                });

                runner.test("with one element array", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(new Covid19DailyReportDataRow[]
                    {
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(5)
                    });
                    test.assertNotNull(dailyReport);
                    test.assertEqual(
                        Iterable.create(
                            Covid19DailyReportDataRow.create()
                                .setCounty("fake-county")
                                .setStateOrProvince("fake-state")
                                .setCountryOrRegion("fake-country")
                                .setConfirmedCases(5)),
                        dailyReport.getDataRows());
                });

                runner.test("with two element array", (Test test) ->
                {
                    final Covid19DailyReport dailyReport = Covid19DailyReport.create(new Covid19DailyReportDataRow[]
                    {
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(5),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(7)
                    });
                    test.assertNotNull(dailyReport);
                    test.assertEqual(
                        Iterable.create(
                            Covid19DailyReportDataRow.create()
                                .setCounty("fake-county")
                                .setStateOrProvince("fake-state")
                                .setCountryOrRegion("fake-country")
                                .setConfirmedCases(5),
                            Covid19DailyReportDataRow.create()
                                .setCounty("fake-county-2")
                                .setStateOrProvince("fake-state")
                                .setCountryOrRegion("fake-country")
                                .setConfirmedCases(7)),
                        dailyReport.getDataRows());
                });
            });

            runner.testGroup("parse(CSVDocument)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReport.parse((CSVDocument)null),
                        new PreConditionFailure("csvDocument cannot be null."));
                });

                final Action3<String,CSVDocument,Iterable<Covid19DailyReportDataRow>> parseTest = (String testName, CSVDocument csvDocument, Iterable<Covid19DailyReportDataRow> expectedDataRows) ->
                {
                    runner.test(testName, (Test test) ->
                    {
                        final Covid19DailyReport dailyReport = Covid19DailyReport.parse(csvDocument).await();
                        test.assertNotNull(dailyReport);
                        test.assertEqual(expectedDataRows, dailyReport.getDataRows());
                    });
                };

                parseTest.run("with empty document", CSVDocument.create(), Iterable.create());

                parseTest.run("with no header row",
                    CSVDocument.create(
                        CSVRow.create("fake-county", "fake-state", "fake-country", "3")),
                    Iterable.create());

                parseTest.run("with only header row",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed")),
                    Iterable.create());

                parseTest.run("with header row and one data row",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and two data rows",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "200"),
                        CSVRow.create("fake-county-2", "fake-state-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with columns in a different order",
                    CSVDocument.create(
                        CSVRow.create("Province_State", "Confirmed", "Admin2", "Country_Region"),
                        CSVRow.create("fake-state", "200", "fake-county", "fake-country"),
                        CSVRow.create("fake-state-2", "300", "fake-county-2", "fake-country-2")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no county column",
                    CSVDocument.create(
                        CSVRow.create("Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-state", "fake-country", "200"),
                        CSVRow.create("fake-state-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no provinceOrState column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-country", "200"),
                        CSVRow.create("fake-county-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no countryOrRegion column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "200"),
                        CSVRow.create("fake-county-2", "fake-state-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no confirmed cases column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region"),
                        CSVRow.create("fake-county", "fake-state", "fake-country"),
                        CSVRow.create("fake-county-2", "fake-state-2", "fake-country-2")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country"),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")));

                parseTest.run("with header row and one data row with empty county",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("", "fake-state", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with empty stateOrProvince",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with empty countryOrRegion",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with negative confirmed cases",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "-200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")));
            });

            runner.testGroup("parse(ByteReadStream)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReport.parse((ByteReadStream)null),
                        new PreConditionFailure("byteReadStream cannot be null."));
                });

                runner.test("with disposed", (Test test) ->
                {
                    final ByteReadStream byteStream = ByteReadStream.create();
                    byteStream.dispose().await();
                    test.assertThrows(() -> Covid19DailyReport.parse(byteStream).await(),
                        new PreConditionFailure("byteReadStream.isDisposed() cannot be true."));
                });

                final Action3<String,CSVDocument,Iterable<Covid19DailyReportDataRow>> parseTest = (String testName, CSVDocument csvDocument, Iterable<Covid19DailyReportDataRow> expectedDataRows) ->
                {
                    runner.test(testName, (Test test) ->
                    {
                        final Covid19DailyReport dailyReport = Covid19DailyReport.parse(csvDocument).await();
                        test.assertNotNull(dailyReport);
                        test.assertEqual(expectedDataRows, dailyReport.getDataRows());
                    });
                };

                parseTest.run("with empty document", CSVDocument.create(), Iterable.create());

                parseTest.run("with no header row",
                    CSVDocument.create(
                        CSVRow.create("fake-county", "fake-state", "fake-country", "3")),
                    Iterable.create());

                parseTest.run("with only header row",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed")),
                    Iterable.create());

                parseTest.run("with header row and one data row",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and two data rows",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "200"),
                        CSVRow.create("fake-county-2", "fake-state-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with columns in a different order",
                    CSVDocument.create(
                        CSVRow.create("Province_State", "Confirmed", "Admin2", "Country_Region"),
                        CSVRow.create("fake-state", "200", "fake-county", "fake-country"),
                        CSVRow.create("fake-state-2", "300", "fake-county-2", "fake-country-2")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no county column",
                    CSVDocument.create(
                        CSVRow.create("Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-state", "fake-country", "200"),
                        CSVRow.create("fake-state-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no provinceOrState column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-country", "200"),
                        CSVRow.create("fake-county-2", "fake-country-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setCountryOrRegion("fake-country-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no countryOrRegion column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "200"),
                        CSVRow.create("fake-county-2", "fake-state-2", "300")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setConfirmedCases(200),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setConfirmedCases(300)));

                parseTest.run("with no confirmed cases column",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region"),
                        CSVRow.create("fake-county", "fake-state", "fake-country"),
                        CSVRow.create("fake-county-2", "fake-state-2", "fake-country-2")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country"),
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county-2")
                            .setStateOrProvince("fake-state-2")
                            .setCountryOrRegion("fake-country-2")));

                parseTest.run("with header row and one data row with empty county",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("", "fake-state", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with empty stateOrProvince",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "", "fake-country", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setCountryOrRegion("fake-country")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with empty countryOrRegion",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "", "200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setConfirmedCases(200)));

                parseTest.run("with header row and one data row with negative confirmed cases",
                    CSVDocument.create(
                        CSVRow.create("Admin2", "Province_State", "Country_Region", "Confirmed"),
                        CSVRow.create("fake-county", "fake-state", "fake-country", "-200")),
                    Iterable.create(
                        Covid19DailyReportDataRow.create()
                            .setCounty("fake-county")
                            .setStateOrProvince("fake-state")
                            .setCountryOrRegion("fake-country")));
            });
        });
    }
}
