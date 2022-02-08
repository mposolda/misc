package org.mposolda.services;

/**
 * This is managed exception, which signals that we failed to download proper candle for some particular company.
 * Hence we needed to fallback into downloading the quote only
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class FailedCandleDownloadException extends Exception {

    // This indicates that we needed to download quote, but fallback to this quote failed to download as well
    private final boolean quoteFallbackFailed;

    public FailedCandleDownloadException(String message, boolean quoteFallbackFailed) {
        super(message);
        this.quoteFallbackFailed = quoteFallbackFailed;
    }

    public boolean isQuoteFallbackFailed() {
        return quoteFallbackFailed;
    }
}
