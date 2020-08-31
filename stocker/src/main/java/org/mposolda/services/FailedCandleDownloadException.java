package org.mposolda.services;

/**
 * This is managed exception, which signals that we failed to download proper candle for some particular company.
 * Hence we needed to fallback into downloading the quote only
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FailedCandleDownloadException extends Exception {

    public FailedCandleDownloadException(String message) {
        super(message);
    }
}
