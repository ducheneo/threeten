/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.builder;

import javax.time.LocalDateTime;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * A field of date/time.
 * <p>
 * A date, as expressed by {@link LocalDateTime}, is broken down into a number of fields,
 * such as year, month, day-of-month, hour, minute and second.
 * Implementations of this interface represent those fields.
 * <p>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface DateTimeField extends CalendricalField {

    /**
     * Gets the range of valid values for the field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * 
     * @return the range of valid values for the field, not null
     */
    DateTimeRuleRange getValueRange();

    /**
     * Gets the unit that the field is measured in.
     * <p>
     * The unit of the field is the period that varies within the range.
     * For example, in the field 'MonthOfYear', the unit is 'Months'.
     * See also {@link #getRangeUnit()}.
     *
     * @return the period unit defining the base unit of the field, not null
     */
    PeriodUnit getBaseUnit();

    /**
     * Gets the range that the field is bound by.
     * <p>
     * The range of the field is the period that the field varies within.
     * For example, in the field 'MonthOfYear', the range is 'Years'.
     * See also {@link #getBaseUnit()}.
     * <p>
     * The range is never null. For example, the 'Year' field is shorthand for
     * 'YearOfForever'. It therefore has a unit of 'Years' and a range of 'Forever'.
     *
     * @return the period unit defining the range of the field, not null
     */
    PeriodUnit getRangeUnit();

    /**
     * Get the rules that the field uses.
     * <p>
     * This method is intended for frameworks rather than day-to-day coding.
     * 
     * @return the rules for the field, not null
     */
    Rules<LocalDateTime> getDateTimeRules();

    //-----------------------------------------------------------------------
    /**
     * The set of rules for manipulating dates and times.
     * <p>
     * This interface defines the internal calculations necessary to manage a field.
     * Applications will primarily deal with {@link DateTimeField}.
     * Each instance of this interface is implicitly associated with a single field.
     * <p>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     * 
     * @param <T> the type of object that the rule works on
     */
    public interface Rules<T> {

        /**
         * Gets the range of valid values for the associated field.
         * <p>
         * All fields can be expressed as a {@code long} integer.
         * This method returns an object that describes the valid range for that value.
         * <p>
         * The date-time object is used to provide context to refine the valid value range.
         * 
         * @param dateTime  the context date-time object, not null
         * @return the range of valid values for the associated field, not null
         */
        DateTimeRuleRange range(T dateTime);

        /**
         * Gets the value of the associated field.
         * <p>
         * The value of the associated field is expressed as a {@code long} integer
         * and is extracted from the specified date-time object.
         * 
         * @param dateTime  the date-time object to query, not null
         * @return the value of the associated field, not null
         */
        long get(T dateTime);

        /**
         * Sets the value of the associated field in the result.
         * <p>
         * The new value of the associated field is expressed as a {@code long} integer.
         * The result will be adjusted to set the value of the associated field.
         * 
         * @param dateTime  the date-time object to adjust, not null
         * @param newValue  the new value of the field
         * @return the adjusted date-time object, not null
         */
        T set(T dateTime, long newValue);

        /**
         * Rolls the value of the associated field in the result.
         * <p>
         * The result will have the associated field rolled by the amount specified.
         * 
         * @param dateTime  the date-time object to adjust, not null
         * @param roll  the amount to roll by
         * @return the adjusted date-time object, not null
         */
        T roll(T dateTime, long roll);
    }

}
