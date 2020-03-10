package qub;

/**
 * A document that contains data about the COVID_19 virus.
 */
public class Covid19DataDocument
{
    private static final Iterable<String> requiredHeaders = Iterable.create("Province/State", "Country/Region", "Lat", "Long");

    private final CSVDocument csvDocument;

    private Covid19DataDocument(CSVDocument csvDocument)
    {
        PreCondition.assertNotNull(csvDocument, "csvDocument");
        PreCondition.assertGreaterThanOrEqualTo(csvDocument.getRowCount(), 1, "csvDocument.getRowCount()");
        PreCondition.assertEqual(Covid19DataDocument.requiredHeaders, csvDocument.getRow(0).getCells().take(Covid19DataDocument.requiredHeaders.getCount()), "csvDocument.getRow(0).getCells().take(Covid19DataDocument.requiredHeaders.getCount())");

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

            final CSVRow headerRow = csvDocument.getRow(0);
            if (headerRow == null)
            {
                throw new ParseException("The first row of the COVID-19 data document must contain " + Covid19DataDocument.requiredHeaders + ".");
            }

            if (!headerRow.getCells().take(Covid19DataDocument.requiredHeaders.getCount()).equals(Covid19DataDocument.requiredHeaders))
            {
                throw new ParseException("The first row of the COVID-19 data document must start with " + Covid19DataDocument.requiredHeaders + ".");
            }

            return new Covid19DataDocument(csvDocument);
        });
    }

    /**
     * Get the dates that have been reported.
     * @return The dates that have been reported.
     */
    public Indexable<DateTime> getDatesReported()
    {
        return this.csvDocument.getRow(0).getCells()
            .skip(Covid19DataDocument.requiredHeaders.getCount())
            .map((String dateString) -> Covid19DataDocument.parseDate(dateString).await())
            .toList();
    }

    /**
     * Get the first date reported.
     * @return The first date reported.
     */
    public DateTime getFirstDateReported()
    {
        return this.getDatesReported().first();
    }

    /**
     * Get the last date reported.
     * @return The last date reported.
     */
    public DateTime getLastDateReported()
    {
        return this.getDatesReported().last();
    }

    /**
     * Get the countries that have been reported.
     * @return The countries that have been reported.
     */
    public Set<String> getCountriesReported()
    {
        return Set.create(this.csvDocument.getRows().skipFirst()
            .map((CSVRow row) -> row.getCell(1))
            .where(Functions.not(Strings::isNullOrEmpty)));
    }

    /**
     * Get the total number of confirmed cases.
     * @return The total number of confirmed cases.
     */
    public int getConfirmedCases()
    {
        return this.getConfirmedCases((CSVRow row) -> true);
    }

    /**
     * Get the total number of confirmed cases.
     * @return The total number of confirmed cases.
     */
    public int getConfirmedCases(Function1<CSVRow,Boolean> rowCondition)
    {
        PreCondition.assertNotNull(rowCondition, "rowCondition");

        return this.getConfirmedCasesInner(null, rowCondition);
    }

    /**
     * Get the total number of confirmed cases as of the provided date.
     * @param date The date to get the total number of confirmed cases by.
     * @return The total number of confirmed cases as of the provided date.
     */
    public int getConfirmedCases(DateTime date)
    {
        PreCondition.assertNotNull(date, "date");

        return this.getConfirmedCases(date, (CSVRow row) -> true);
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

    /**
     * Get the total number of confirmed cases as of the provided date.
     * @param date The date to get the total number of confirmed cases by or null to get the latest.
     * @return The total number of confirmed cases as of the provided date.
     */
    public int getConfirmedCasesInner(DateTime date, Function1<CSVRow,Boolean> rowCondition)
    {
        PreCondition.assertNotNull(rowCondition, "rowCondition");

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

        int result = 0;
        if (dateIndex != -1)
        {
            final int latestConfirmedCasesColumnIndex = Covid19DataDocument.requiredHeaders.getCount() + dateIndex;
            final Iterable<Integer> confirmedCases = this.csvDocument.getRows().skipFirst()
                .where(rowCondition)
                .map((CSVRow row) -> Integers.parse(row.getCell(latestConfirmedCasesColumnIndex).trim()).await());
            result = Integers.sum(confirmedCases);
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
            final int year = Integers.parse(dateParts[2].trim()).await() + 2000;
            return DateTime.create(year, month, day);
        });
    }
}
