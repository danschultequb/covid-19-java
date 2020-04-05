package qub;

/**
 * A report of a single day from the Covid-19 data set.
 */
public class Covid19DailyReport
{
    private final Iterable<Covid19DailyReportDataRow> dataRows;

    private Covid19DailyReport(Iterable<Covid19DailyReportDataRow> dataRows)
    {
        PreCondition.assertNotNull(dataRows, "dataRows");

        this.dataRows = dataRows;
    }

    public static Covid19DailyReport create(Iterable<Covid19DailyReportDataRow> dataRows)
    {
        PreCondition.assertNotNull(dataRows, "dataRows");

        return new Covid19DailyReport(dataRows);
    }

    public static Covid19DailyReport create(Covid19DailyReportDataRow... dataRows)
    {
        PreCondition.assertNotNull(dataRows, "dataRows");

        return Covid19DailyReport.create(Iterable.create(dataRows));
    }

    public static Result<Covid19DailyReport> parse(CSVDocument csvDocument)
    {
        PreCondition.assertNotNull(csvDocument, "csvDocument");

        return Result.create(() ->
        {
            final List<Covid19DailyReportDataRow> dataRows = List.create();

            if (csvDocument.getRowCount() > 0)
            {
                final Covid19DailyReportColumnMapping columnMapping = Covid19DailyReportColumnMapping.parse(csvDocument.getRow(0)).await();
                for (final CSVRow csvRow : csvDocument.getRows().skipFirst())
                {
                    final Covid19DailyReportDataRow dataRow = Covid19DailyReportDataRow.create();

                    final String county = columnMapping.getCounty(csvRow).catchError().await();
                    if (!Strings.isNullOrEmpty(county))
                    {
                        dataRow.setCounty(county);
                    }

                    final String stateOrProvince = columnMapping.getStateOrProvince(csvRow).catchError().await();
                    if (!Strings.isNullOrEmpty(stateOrProvince))
                    {
                        dataRow.setStateOrProvince(stateOrProvince);
                    }

                    final String countryOrRegion = columnMapping.getCountryOrRegion(csvRow).catchError().await();
                    if (!Strings.isNullOrEmpty(countryOrRegion))
                    {
                        dataRow.setCountryOrRegion(countryOrRegion);
                    }

                    final Integer confirmedCases = columnMapping.getConfirmedCases(csvRow).catchError().await();
                    if (confirmedCases != null && confirmedCases >= 0)
                    {
                        dataRow.setConfirmedCases(confirmedCases);
                    }

                    dataRows.add(dataRow);
                }
            }

            return Covid19DailyReport.create(dataRows);
        });
    }

    public static Result<Covid19DailyReport> parse(ByteReadStream byteReadStream)
    {
        PreCondition.assertNotNull(byteReadStream, "byteReadStream");

        return Result.create(() ->
        {
            final CSVDocument csvDocument = CSV.parse(byteReadStream).await();
            return Covid19DailyReport.parse(csvDocument).await();
        });
    }

    public static Result<Covid19DailyReport> parse(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            try (final ByteReadStream fileContentByteReadStream = new BufferedByteReadStream(file.getContentByteReadStream().await()))
            {
                return Covid19DailyReport.parse(fileContentByteReadStream).await();
            }
        });
    }

    public Iterable<Covid19DailyReportDataRow> getDataRows()
    {
        return this.dataRows;
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof Covid19DailyReport && this.equals((Covid19DailyReport)rhs);
    }

    public boolean equals(Covid19DailyReport rhs)
    {
        return rhs != null &&
            this.dataRows.equals(rhs.dataRows);
    }
}
