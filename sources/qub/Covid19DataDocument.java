package qub;

/**
 * A document that contains data about the COVID_19 virus.
 */
public class Covid19DataDocument
{
    public static final Iterable<String> requiredHeaders = Iterable.create("Province/State", "Country/Region", "Lat", "Long");

    private final CSVDocument csvDocument;

    private Covid19DataDocument(CSVDocument csvDocument)
    {
        PreCondition.assertNotNull(csvDocument, "csvDocument");
        PreCondition.assertGreaterThanOrEqualTo(csvDocument.getRowCount(), 1, "csvDocument.getRowCount()");
        PreCondition.assertEqual(Covid19DataDocument.requiredHeaders, Covid19DataDocument.getHeaderRow(csvDocument).getCells().take(Covid19DataDocument.requiredHeaders.getCount()), "Covid19DataDocument.getHeaderRow(csvDocument).getCells().take(Covid19DataDocument.requiredHeaders.getCount())");

        this.csvDocument = csvDocument;
    }

    /**
     * Create a new Covid19DataDocument.
     * @return A new Covid19DataDocument.
     */
    public static Covid19DataDocument create()
    {
        return new Covid19DataDocument(CSVDocument.create()
            .addRow(CSVRow.create(Covid19DataDocument.requiredHeaders)));
    }

    /**
     * Parse a Covid19DataDocument from the provided ByteReadStream.
     * @param byteReadStream The ByteReadStream to parse a Covid19DataDocument from.
     * @return The parsed Covid19DataDocument.
     */
    public static Result<Covid19DataDocument> parse(ByteReadStream byteReadStream)
    {
        PreCondition.assertNotNull(byteReadStream, "byteReadStream");

        return Covid19DataDocument.parse(byteReadStream.asCharacterReadStream());
    }

    /**
     * Parse a Covid19DataDocument from the provided characters.
     * @param characters The characters to parse a Covid19DataDocument from.
     * @return The parsed Covid19DataDocument.
     */
    public static Result<Covid19DataDocument> parse(String characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Covid19DataDocument.parse(Strings.iterate(characters));
    }

    /**
     * Parse a Covid19DataDocument from the provided characters.
     * @param characters The characters to parse a Covid19DataDocument from.
     * @return The parsed Covid19DataDocument.
     */
    public static Result<Covid19DataDocument> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            final CSVDocument csvDocument = CSV.parse(characters).await();
            return Covid19DataDocument.parse(csvDocument).await();
        });
    }

    /**
     * Parse a Covid19DataDocument from the provided CSVDocument.
     * @param csvDocument The CSV document to parse a Covid19DataDocument from.
     * @return The parsed Covid19DataDocument.
     */
    public static Result<Covid19DataDocument> parse(CSVDocument csvDocument)
    {
        PreCondition.assertNotNull(csvDocument, "csvDocument");

        return Result.create(() ->
        {
            if (csvDocument.getRowCount() == 0)
            {
                throw new ParseException("A COVID-19 data document must contain a header row that starts with " + Covid19DataDocument.requiredHeaders + ".");
            }

            final CSVRow headerRow = Covid19DataDocument.getHeaderRow(csvDocument);
            if (!headerRow.getCells().take(Covid19DataDocument.requiredHeaders.getCount()).equals(Covid19DataDocument.requiredHeaders))
            {
                throw new ParseException("The first row of the COVID-19 data document must start with " + Covid19DataDocument.requiredHeaders + ".");
            }

            return new Covid19DataDocument(csvDocument);
        });
    }

    /**
     * Get the header row that contains the dates reported.
     * @return The header row that contains the dates reported.
     */
    private CSVRow getHeaderRow()
    {
        return Covid19DataDocument.getHeaderRow(this.csvDocument);
    }

    private static CSVRow getHeaderRow(CSVDocument csvDocument)
    {
        PreCondition.assertNotNull(csvDocument, "csvDocument");

        return csvDocument.getRow(0);
    }

    private Iterable<CSVRow> getDataRows()
    {
        return this.csvDocument.getRows().skipFirst();
    }

    /**
     * Get the dates that have been reported.
     * @return The dates that have been reported.
     */
    public Indexable<DateTime> getDatesReported()
    {
        return this.getHeaderRow().getCells()
            .skip(Covid19DataDocument.requiredHeaders.getCount())
            .map((String dateString) -> Covid19DataDocument.parseDate(dateString).await())
            .toList();
    }

    /**
     * Get the dates that have been reported as of the provided date.
     * @param asOf The maximum date to report.
     * @return The dates that have been reported as of the provided Date.
     */
    public Indexable<DateTime> getDatesReported(DateTime asOf)
    {
        PreCondition.assertNotNull(asOf, "asOf");

        return this.getDatesReported()
            .where((DateTime date) -> date.lessThanOrEqualTo(asOf))
            .toList();
    }

    /**
     * Get the countries that have been reported.
     * @return The countries that have been reported.
     */
    public Set<String> getCountriesReported()
    {
        return Set.create(this.getDataRows()
            .map(Covid19DataDocument::getCountry)
            .where(Functions.not(Strings::isNullOrEmpty)));
    }

    /**
     * Get the countries that have been reported.
     * @return The countries that have been reported.
     */
    public Set<String> getCountriesReported(DateTime asOf)
    {
        PreCondition.assertNotNull(asOf, "asOf");

        final int confirmedCasesIndex = this.getConfirmedCasesIndex(asOf);
        final Set<String> result = Set.create();
        if (confirmedCasesIndex != -1)
        {
            result.addAll(this.getDataRows()
                .map((CSVRow row) ->
                    0 < Covid19DataDocument.getConfirmedCasesFromConfirmedCasesIndex(row, confirmedCasesIndex)
                        ? Covid19DataDocument.getCountry(row)
                        : null)
                .where(Functions.not(Strings::isNullOrEmpty)));
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }

    /**
     * Get the total number of confirmed cases.
     * @return The total number of confirmed cases.
     */
    public int getConfirmedCases()
    {
        return this.getConfirmedCasesInner(null, (CSVRow row) -> true);
    }

    /**
     * Get the total number of confirmed cases as of the provided date.
     * @param date The date to get the total number of confirmed cases by.
     * @return The total number of confirmed cases as of the provided date.
     */
    public int getConfirmedCases(DateTime date, Function1<CSVRow,Boolean> rowCondition)
    {
        PreCondition.assertNotNull(date, "date");
        PreCondition.assertNotNull(rowCondition, "rowCondition");

        return this.getConfirmedCasesInner(date, rowCondition);
    }

    private int getDateIndex(DateTime date)
    {
        final Indexable<DateTime> datesReported = this.getDatesReported();
        int dateIndex = -1;
        if (date != null)
        {
            dateIndex = datesReported.indexOf((DateTime dateReported) ->
                dateReported.getYear() == date.getYear() &&
                dateReported.getMonth() == date.getMonth() &&
                dateReported.getDayOfMonth() == date.getDayOfMonth());
        }

        if (dateIndex == -1)
        {
            final DateTime lastReportedDate = datesReported.last();
            if (date == null || date.greaterThan(lastReportedDate))
            {
                dateIndex = datesReported.getCount() - 1;
            }
        }

        PostCondition.assertGreaterThanOrEqualTo(dateIndex, -1, "dateIndex");

        return dateIndex;
    }

    private int getConfirmedCasesIndex(DateTime date)
    {
        final int dateIndex = this.getDateIndex(date);
        final int result = Covid19DataDocument.getConfirmedCasesIndex(dateIndex);

        PostCondition.assertGreaterThanOrEqualTo(result, -1, "result");

        return result;
    }

    private static int getConfirmedCasesIndex(int dateIndex)
    {
        PreCondition.assertGreaterThanOrEqualTo(dateIndex, -1, "dateIndex");

        final int result = dateIndex == -1
            ? -1
            : dateIndex + Covid19DataDocument.requiredHeaders.getCount();

        PostCondition.assertGreaterThanOrEqualTo(result, -1, "result");

        return result;
    }

    /**
     * Get the total number of confirmed cases as of the provided date.
     * @param date The date to get the total number of confirmed cases by or null to get the latest.
     * @return The total number of confirmed cases as of the provided date.
     */
    public int getConfirmedCasesInner(DateTime date, Function1<CSVRow,Boolean> rowCondition)
    {
        PreCondition.assertNotNull(rowCondition, "rowCondition");

        int result = 0;
        final int confirmedCasesIndex = this.getConfirmedCasesIndex(date);
        if (confirmedCasesIndex != -1)
        {
            result = Integers.sum(this.getDataRows()
                .where(rowCondition)
                .map((CSVRow row) -> Covid19DataDocument.getConfirmedCasesFromConfirmedCasesIndex(row, confirmedCasesIndex)));
        }

        PostCondition.assertGreaterThanOrEqualTo(result, 0, "result");

        return result;
    }

    /**
     * Parse a DateTime object from the provided dateString.
     * @param dateString A date string from a COVID-19 data document.
     * @return The parsed DateTime object.
     */
    private static Result<DateTime> parseDate(String dateString)
    {
        PreCondition.assertNotNullAndNotEmpty(dateString, "dateString");

        return Result.create(() ->
        {
            final String[] dateParts = dateString.split("/");
            final int month = Integers.parse(dateParts[0]).await();
            final int day = Integers.parse(dateParts[1]).await();
            final int year = Integers.parse(dateParts[2]).await() + 2000;
            return DateTime.create(year, month, day);
        });
    }

    private static String getCountry(CSVRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        return dataRow.getCell(1);
    }

    private static int getConfirmedCasesFromConfirmedCasesIndex(CSVRow dataRow, int confirmedCasesIndex)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");
        PreCondition.assertGreaterThanOrEqualTo(confirmedCasesIndex, Covid19DataDocument.requiredHeaders.getCount(), "confirmedCasesIndex");

        Integer result = null;

        final String confirmedCasesString = dataRow.getCell(confirmedCasesIndex);
        if (!Strings.isNullOrEmpty(confirmedCasesString))
        {
            result = Integers.parse(confirmedCasesString).catchError().await();
        }

        if (result == null)
        {
            result = 0;
        }

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}
