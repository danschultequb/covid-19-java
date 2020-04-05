package qub;

public interface Covid19DailyReportColumnMappingTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(Covid19DailyReportColumnMapping.class, () ->
        {
            runner.test("create()", (Test test) ->
            {
                final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.create();
                test.assertNotNull(mapping);
                test.assertFalse(mapping.hasCountyColumn());
                test.assertFalse(mapping.hasStateOrProvinceColumn());
                test.assertFalse(mapping.hasCountryOrRegionColumn());
                test.assertFalse(mapping.hasConfirmedCasesColumn());
            });

            runner.testGroup("parse(CSVRow)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse((CSVRow)null),
                        new PreConditionFailure("headerRow cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(CSVRow.create()).await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with unrecognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(CSVRow.create("FIPS", "Tacos", "Burritos")).await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with recognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(CSVRow.create("Confirmed", "Country_Region", "Admin2", "Province_State")).await();
                    test.assertNotNull(mapping);
                    test.assertTrue(mapping.hasCountyColumn());
                    test.assertEqual(2, mapping.getCountyColumnIndex());
                    test.assertTrue(mapping.hasStateOrProvinceColumn());
                    test.assertEqual(3, mapping.getStateOrProvinceColumnIndex());
                    test.assertTrue(mapping.hasCountryOrRegionColumn());
                    test.assertEqual(1, mapping.getCountryOrRegionColumnIndex());
                    test.assertTrue(mapping.hasConfirmedCasesColumn());
                    test.assertEqual(0, mapping.getConfirmedCasesColumnIndex());
                });

                runner.test("with multiple county column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(CSVRow.create("Admin2", "admin2")).await(),
                        new ParseException("The daily report has multiple columns with a recognized county header [Admin2]"));
                });

                runner.test("with multiple state/province column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(CSVRow.create("Province_State", "Province_State")).await(),
                        new ParseException("The daily report has multiple columns with a recognized state/province header [Province_State,Province/State]"));
                });

                runner.test("with multiple country/region column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(CSVRow.create("Country_Region", "Country_Region")).await(),
                        new ParseException("The daily report has multiple columns with a recognized country/region header [Country_Region,Country/Region]"));
                });

                runner.test("with multiple confirmed cases column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(CSVRow.create("Confirmed", "Confirmed")).await(),
                        new ParseException("The daily report has multiple columns with a recognized confirmed cases header [Confirmed]"));
                });
            });

            runner.testGroup("parse(String...)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse((String[])null),
                        new PreConditionFailure("headerRow cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(new String[0]).await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with unrecognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse("FIPS", "Tacos", "Burritos").await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with recognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse("Confirmed", "Country_Region", "Admin2", "Province_State").await();
                    test.assertNotNull(mapping);
                    test.assertTrue(mapping.hasCountyColumn());
                    test.assertEqual(2, mapping.getCountyColumnIndex());
                    test.assertTrue(mapping.hasStateOrProvinceColumn());
                    test.assertEqual(3, mapping.getStateOrProvinceColumnIndex());
                    test.assertTrue(mapping.hasCountryOrRegionColumn());
                    test.assertEqual(1, mapping.getCountryOrRegionColumnIndex());
                    test.assertTrue(mapping.hasConfirmedCasesColumn());
                    test.assertEqual(0, mapping.getConfirmedCasesColumnIndex());
                });

                runner.test("with multiple county column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse("Admin2", "admin2").await(),
                        new ParseException("The daily report has multiple columns with a recognized county header [Admin2]"));
                });

                runner.test("with multiple state/province column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse("Province_State", "Province_State").await(),
                        new ParseException("The daily report has multiple columns with a recognized state/province header [Province_State,Province/State]"));
                });

                runner.test("with multiple country/region column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse("Country_Region", "Country_Region").await(),
                        new ParseException("The daily report has multiple columns with a recognized country/region header [Country_Region,Country/Region]"));
                });

                runner.test("with multiple confirmed cases column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse("Confirmed", "Confirmed").await(),
                        new ParseException("The daily report has multiple columns with a recognized confirmed cases header [Confirmed]"));
                });
            });

            runner.testGroup("parse(Iterable<String>)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse((Iterable<String>)null),
                        new PreConditionFailure("headerRow cannot be null."));
                });

                runner.test("with empty", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(Iterable.create()).await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with unrecognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(Iterable.create("FIPS", "Tacos", "Burritos")).await();
                    test.assertNotNull(mapping);
                    test.assertFalse(mapping.hasCountyColumn());
                    test.assertFalse(mapping.hasStateOrProvinceColumn());
                    test.assertFalse(mapping.hasCountryOrRegionColumn());
                    test.assertFalse(mapping.hasConfirmedCasesColumn());
                });

                runner.test("with recognized columns", (Test test) ->
                {
                    final Covid19DailyReportColumnMapping mapping = Covid19DailyReportColumnMapping.parse(Iterable.create("Confirmed", "Country_Region", "Admin2", "Province_State")).await();
                    test.assertNotNull(mapping);
                    test.assertTrue(mapping.hasCountyColumn());
                    test.assertEqual(2, mapping.getCountyColumnIndex());
                    test.assertTrue(mapping.hasStateOrProvinceColumn());
                    test.assertEqual(3, mapping.getStateOrProvinceColumnIndex());
                    test.assertTrue(mapping.hasCountryOrRegionColumn());
                    test.assertEqual(1, mapping.getCountryOrRegionColumnIndex());
                    test.assertTrue(mapping.hasConfirmedCasesColumn());
                    test.assertEqual(0, mapping.getConfirmedCasesColumnIndex());
                });

                runner.test("with multiple county column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(Iterable.create("Admin2", "admin2")).await(),
                        new ParseException("The daily report has multiple columns with a recognized county header [Admin2]"));
                });

                runner.test("with multiple state/province column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(Iterable.create("Province_State", "Province_State")).await(),
                        new ParseException("The daily report has multiple columns with a recognized state/province header [Province_State,Province/State]"));
                });

                runner.test("with multiple country/region column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(Iterable.create("Country_Region", "Country_Region")).await(),
                        new ParseException("The daily report has multiple columns with a recognized country/region header [Country_Region,Country/Region]"));
                });

                runner.test("with multiple confirmed cases column matches", (Test test) ->
                {
                    test.assertThrows(() -> Covid19DailyReportColumnMapping.parse(Iterable.create("Confirmed", "Confirmed")).await(),
                        new ParseException("The daily report has multiple columns with a recognized confirmed cases header [Confirmed]"));
                });
            });
        });
    }
}
