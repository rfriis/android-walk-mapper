package uk.ac.abertay.cmp309.WalkMapper;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAxisValueFormatter extends ValueFormatter {

    private long referenceTimestamp; // this is the ealiest timestamp
    private DateFormat mDataFormat;
    private Date mDate;

    public DateAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
        this.mDataFormat = new SimpleDateFormat("dd/MM");
        this.mDate = new Date();
    }

    @Override
    public String getFormattedValue(float value) {
        long convertedTimestamp = (long) value;
        long originalTimestamp = referenceTimestamp + convertedTimestamp;
        return getDate(originalTimestamp);
    }

    public String getDate(long timestamp) {
        mDate.setTime(timestamp);
        return mDataFormat.format(mDate);
    }
}
