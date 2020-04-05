package qub;

/**
 * A mapping that determines which columns map to which data values.
 */
public class Covid19DailyReportColumnMapping
{
    private static final Iterable<String> countyHeaderNames = Iterable.create("Admin2");
    private static final Iterable<String> stateOrProvinceHeaderNames = Iterable.create("Province_State", "Province/State");
    private static final Iterable<String> countryOrRegionHeaderNames = Iterable.create("Country_Region", "Country/Region");
    private static final Iterable<String> confirmedCasesHeaderNames = Iterable.create("Confirmed");

    private Integer countyColumnIndex;
    private Integer stateOrProvinceColumnIndex;
    private Integer countryOrRegionColumnIndex;
    private Integer confirmedCasesColumnIndex;

    private Covid19DailyReportColumnMapping()
    {
    }

    public static Covid19DailyReportColumnMapping create()
    {
        return new Covid19DailyReportColumnMapping();
    }

    public static Result<Covid19DailyReportColumnMapping> parse(CSVRow headerRow)
    {
        PreCondition.assertNotNull(headerRow, "headerRow");

        return Covid19DailyReportColumnMapping.parse(headerRow.getCells());
    }

    public static Result<Covid19DailyReportColumnMapping> parse(String... headerRow)
    {
        PreCondition.assertNotNull(headerRow, "headerRow");

        return Covid19DailyReportColumnMapping.parse(Iterable.create(headerRow));
    }

    public static Result<Covid19DailyReportColumnMapping> parse(Iterable<String> headerRow)
    {
        PreCondition.assertNotNull(headerRow, "headerRow");

        return Result.create(() ->
        {
            final Covid19DailyReportColumnMapping result = Covid19DailyReportColumnMapping.create();
            int columnIndex = 0;
            for (final String columnHeader : headerRow)
            {
                if (Covid19DailyReportColumnMapping.columnHeaderMatches(columnHeader, Covid19DailyReportColumnMapping.countyHeaderNames))
                {
                    if (result.hasCountyColumn())
                    {
                        throw new ParseException("The daily report has multiple columns with a recognized county header " + Covid19DailyReportColumnMapping.countyHeaderNames);
                    }
                    else
                    {
                        result.setCountyColumnIndex(columnIndex);
                    }
                }
                else if (Covid19DailyReportColumnMapping.columnHeaderMatches(columnHeader, Covid19DailyReportColumnMapping.stateOrProvinceHeaderNames))
                {
                    if (result.hasStateOrProvinceColumn())
                    {
                        throw new ParseException("The daily report has multiple columns with a recognized state/province header " + Covid19DailyReportColumnMapping.stateOrProvinceHeaderNames);
                    }
                    else
                    {
                        result.setStateOrProvinceColumnIndex(columnIndex);
                    }
                }
                else if (Covid19DailyReportColumnMapping.columnHeaderMatches(columnHeader, Covid19DailyReportColumnMapping.countryOrRegionHeaderNames))
                {
                    if (result.hasCountryOrRegionColumn())
                    {
                        throw new ParseException("The daily report has multiple columns with a recognized country/region header " + Covid19DailyReportColumnMapping.countryOrRegionHeaderNames);
                    }
                    else
                    {
                        result.setCountryOrRegionColumnIndex(columnIndex);
                    }
                }
                else if (Covid19DailyReportColumnMapping.columnHeaderMatches(columnHeader, Covid19DailyReportColumnMapping.confirmedCasesHeaderNames))
                {
                    if (result.hasConfirmedCasesColumn())
                    {
                        throw new ParseException("The daily report has multiple columns with a recognized confirmed cases header " + Covid19DailyReportColumnMapping.confirmedCasesHeaderNames);
                    }
                    else
                    {
                        result.setConfirmedCasesColumnIndex(columnIndex);
                    }
                }

                ++columnIndex;
            }

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    private static boolean columnHeaderMatches(String columnHeader, Iterable<String> columnHeaderOptions)
    {
        PreCondition.assertNotNull(columnHeader, "columnHeader");
        PreCondition.assertNotNull(columnHeaderOptions, "columnHeaderOptions");

        return columnHeaderOptions.contains(columnHeader, String::equalsIgnoreCase);
    }

    public Integer getCountyColumnIndex()
    {
        return this.countyColumnIndex;
    }

    public Covid19DailyReportColumnMapping setCountyColumnIndex(int countyColumnIndex)
    {
        PreCondition.assertGreaterThanOrEqualTo(countyColumnIndex, 0, "countyColumnIndex");

        this.countyColumnIndex = countyColumnIndex;

        return this;
    }

    public boolean hasCountyColumn()
    {
        return this.countyColumnIndex != null;
    }

    public Result<String> getCounty(CSVRow csvRow)
    {
        PreCondition.assertNotNull(csvRow, "csvRow");

        return Result.create(() ->
        {
            if (!this.hasCountyColumn())
            {
                throw new NotSupportedException("No county column index was detected.");
            }

            final int countyColumnIndex = this.getCountyColumnIndex();
            if (csvRow.getCellCount() <= countyColumnIndex)
            {
                throw new NotFoundException("No county column was found in the CSV row.");
            }

            return csvRow.getCell(countyColumnIndex);
        });
    }

    public Integer getStateOrProvinceColumnIndex()
    {
        return this.stateOrProvinceColumnIndex;
    }

    public Covid19DailyReportColumnMapping setStateOrProvinceColumnIndex(int stateOrProvinceColumnIndex)
    {
        PreCondition.assertGreaterThanOrEqualTo(stateOrProvinceColumnIndex, 0, "stateOrProvinceColumnIndex");

        this.stateOrProvinceColumnIndex = stateOrProvinceColumnIndex;

        return this;
    }

    public boolean hasStateOrProvinceColumn()
    {
        return this.stateOrProvinceColumnIndex != null;
    }

    public Result<String> getStateOrProvince(CSVRow csvRow)
    {
        PreCondition.assertNotNull(csvRow, "csvRow");

        return Result.create(() ->
        {
            if (!this.hasStateOrProvinceColumn())
            {
                throw new NotSupportedException("No county column index was detected.");
            }

            final int stateOrProvinceColumnIndex = this.getStateOrProvinceColumnIndex();
            if (csvRow.getCellCount() <= stateOrProvinceColumnIndex)
            {
                throw new NotFoundException("No state/province column was found in the CSV row.");
            }

            return csvRow.getCell(stateOrProvinceColumnIndex);
        });
    }

    public Integer getCountryOrRegionColumnIndex()
    {
        return this.countryOrRegionColumnIndex;
    }

    public Covid19DailyReportColumnMapping setCountryOrRegionColumnIndex(int countryOrRegionColumnIndex)
    {
        PreCondition.assertGreaterThanOrEqualTo(countryOrRegionColumnIndex, 0, "countryOrRegionColumnIndex");

        this.countryOrRegionColumnIndex = countryOrRegionColumnIndex;

        return this;
    }

    public boolean hasCountryOrRegionColumn()
    {
        return this.countryOrRegionColumnIndex != null;
    }

    public Result<String> getCountryOrRegion(CSVRow csvRow)
    {
        PreCondition.assertNotNull(csvRow, "csvRow");

        return Result.create(() ->
        {
            if (!this.hasCountryOrRegionColumn())
            {
                throw new NotSupportedException("No country/region column index was detected.");
            }

            final int countryOrRegionColumnIndex = this.getCountryOrRegionColumnIndex();
            if (csvRow.getCellCount() <= countryOrRegionColumnIndex)
            {
                throw new NotFoundException("No country/region column was found in the CSV row.");
            }

            return csvRow.getCell(countryOrRegionColumnIndex);
        });
    }

    public Integer getConfirmedCasesColumnIndex()
    {
        return this.confirmedCasesColumnIndex;
    }

    public Covid19DailyReportColumnMapping setConfirmedCasesColumnIndex(int confirmedCasesColumnIndex)
    {
        PreCondition.assertGreaterThanOrEqualTo(confirmedCasesColumnIndex, 0, "confirmedCasesColumnIndex");

        this.confirmedCasesColumnIndex = confirmedCasesColumnIndex;

        return this;
    }

    public boolean hasConfirmedCasesColumn()
    {
        return this.confirmedCasesColumnIndex != null;
    }

    public Result<Integer> getConfirmedCases(CSVRow csvRow)
    {
        PreCondition.assertNotNull(csvRow, "csvRow");

        return Result.create(() ->
        {
            if (!this.hasConfirmedCasesColumn())
            {
                throw new NotSupportedException("No confirmed cases column index was detected.");
            }

            final int confirmedCasesColumnIndex = this.getConfirmedCasesColumnIndex();
            if (csvRow.getCellCount() <= confirmedCasesColumnIndex)
            {
                throw new NotFoundException("No confirmed cases column was found in the CSV row.");
            }

            final String confirmedCasesString = csvRow.getCell(confirmedCasesColumnIndex);
            final Integer result = Integers.parse(confirmedCasesString).await();
            if (result < 0)
            {
                throw new ParseException("Confirmed cases cannot be negative (" + result + ").");
            }

            return result;
        });
    }
}
