package clearcontrol.gui.jfx.custom.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * A DateTimePicker with configurable datetime format where both date and time
 * can be changed via the text field and the date can additionally be changed
 * via the JavaFX default date picker.
 * 
 * from:
 * https://stackoverflow.com/questions/28493097/is-there-any-date-and-time-picker-available-for-javafx
 */
public class DateTimePicker extends DatePicker
{

  /**
   * Default date and time format.
   */
  public static final String DefaultFormat = "yyyy-MM-dd HH:mm";

  private DateTimeFormatter formatter;
  private ObjectProperty<LocalDateTime> mDateTimeValueProperty =
                                                               new SimpleObjectProperty<>(LocalDateTime.now());
  private ObjectProperty<String> mDateAndTimeFormat =
                                                    new SimpleObjectProperty<String>()
                                                    {
                                                      @Override
                                                      public void set(String newValue)
                                                      {
                                                        super.set(newValue);
                                                        formatter =
                                                                  DateTimeFormatter.ofPattern(newValue);
                                                      }
                                                    };

  /**
   * Instanciates a local date and time picker
   */
  public DateTimePicker()
  {
    getStyleClass().add("datetime-picker");
    setFormat(DefaultFormat);
    InternalConverter lInternalConverter = new InternalConverter();
    setConverter(lInternalConverter);

    // Syncronize changes to the underlying date value back to the dateTimeValue
    valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null)
      {
        mDateTimeValueProperty.set(null);
      }
      else
      {
        if (mDateTimeValueProperty.get() == null)
        {
          mDateTimeValueProperty.set(LocalDateTime.of(newValue,
                                                      LocalTime.now()));
        }
        else
        {
          LocalTime time = mDateTimeValueProperty.get().toLocalTime();
          mDateTimeValueProperty.set(LocalDateTime.of(newValue,
                                                      time));
        }
      }
    });

    // Syncronize changes to dateTimeValue back to the underlying date value
    mDateTimeValueProperty.addListener((observable, o, n) -> {
      if (o != n)
      {
        LocalDate lLocalDate = n.toLocalDate();
        setValue(n == null ? null : lLocalDate);
        getEditor().setText(lInternalConverter.toString(lLocalDate));
      }
    });

    // Persist changes onblur
    getEditor().focusedProperty()
               .addListener((observable, oldValue, newValue) -> {
                 if (!newValue)
                   simulateEnterPressed();
               });

  }

  private void simulateEnterPressed()
  {
    getEditor().fireEvent(new KeyEvent(getEditor(),
                                       getEditor(),
                                       KeyEvent.KEY_PRESSED,
                                       null,
                                       null,
                                       KeyCode.ENTER,
                                       false,
                                       false,
                                       false,
                                       false));
  }

  /**
   * Returns picked local date and time
   * 
   * @return picked local date and time
   */
  public LocalDateTime getDateTimeValue()
  {
    return mDateTimeValueProperty.get();
  }

  /**
   * Sets picked local date and time
   * 
   * @param pDateTimeValue
   *          date and time value
   */
  public void setDateTimeValue(LocalDateTime pDateTimeValue)
  {
    this.mDateTimeValueProperty.set(pDateTimeValue);
  }

  /**
   * Returns date and time property
   * 
   * @return date and time property
   */
  public ObjectProperty<LocalDateTime> getDateTimeValueProperty()
  {
    return mDateTimeValueProperty;
  }

  /**
   * Returns date and time format string
   * 
   * @return format string
   */
  public String getFormat()
  {
    return mDateAndTimeFormat.get();
  }

  /**
   * Retuns date and time format property
   * 
   * @return date and time format property
   */
  public ObjectProperty<String> formatProperty()
  {
    return mDateAndTimeFormat;
  }

  /**
   * Sets date and time format
   * 
   * @param pFormat
   *          date and time format
   */
  public void setFormat(String pFormat)
  {
    this.mDateAndTimeFormat.set(pFormat);
  }

  private class InternalConverter extends StringConverter<LocalDate>
  {
    @Override
    public String toString(LocalDate object)
    {
      LocalDateTime value = getDateTimeValue();
      return (value != null) ? value.format(formatter) : "";
    }

    @Override
    public LocalDate fromString(String value)
    {
      if (value == null)
      {
        mDateTimeValueProperty.set(null);
        return null;
      }

      mDateTimeValueProperty.set(LocalDateTime.parse(value,
                                                     formatter));
      return mDateTimeValueProperty.get().toLocalDate();
    }
  }
}
