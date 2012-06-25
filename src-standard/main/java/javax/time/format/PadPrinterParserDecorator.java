/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

/**
 * Pads the output to a fixed width.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class PadPrinterParserDecorator implements DateTimePrinterParser {

    /**
     * The printer/parser to decorate.
     */
    private final DateTimePrinterParser printerParser;
    /**
     * The width to pad the next field to.
     */
    private final int padWidth;
    /**
     * The character to pad the next field with.
     */
    private final char padChar;

    /**
     * Constructor.
     *
     * @param printerParser  the printer, not null
     * @param padWidth  the width to pad to, 1 or greater
     * @param padChar  the pad character
     */
    PadPrinterParserDecorator(DateTimePrinterParser printerParser, int padWidth, char padChar) {
        // input checked by DateTimeFormatterBuilder
        this.printerParser = printerParser;
        this.padWidth = padWidth;
        this.padChar = padChar;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        int preLen = buf.length();
        if (printerParser.print(context, buf) == false) {
            return false;
        }
        int len = buf.length() - preLen;
        if (len > padWidth) {
            throw new CalendricalPrintException(
                "Cannot print as output of " + len + " characters exceeds pad width of " + padWidth);
        }
        for (int i = 0; i < padWidth - len; i++) {
            buf.insert(preLen, padChar);
        }
        return true;
    }

    //-----------------------------------------------------------------------
    @Override
    public int parse(DateTimeParseContext context, CharSequence text, int position) {
        if (position > text.length()) {
            throw new IndexOutOfBoundsException();
        }
        int endPos = position + padWidth;
        if (endPos > text.length()) {
            return ~position;  // not enough characters in the string to meet the parse width
        }
        int pos = position;
        while (pos < endPos && text.charAt(pos) == padChar) {
            pos++;
        }
        text = text.subSequence(0, endPos);
        int firstError = 0;
        while (pos >= position) {
            int resultPos = printerParser.parse(context, text, pos);
            if (resultPos < 0) {
                // parse of decorated field had an error
                if (firstError == 0) {
                    firstError = resultPos;
                }
                // loop around in case the decorated parser can handle the padChar at the start
                pos--;
                continue;
            }
            if (resultPos != endPos) {
                return ~position;  // parse of decorated field didn't parse to the end
            }
            return resultPos;
        }
        // loop runs at least once, so firstError must be set by the time we get here
        return firstError;  // return error from first parse of decorated field
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return "Pad(" + printerParser + "," + padWidth + (padChar == ' ' ? ")" : ",'" + padChar + "')");
    }

}