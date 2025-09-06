package com.upfault.fault.api.types;

import org.jetbrains.annotations.NotNull;

/**
 * Immutable snapshot of server TPS (ticks per second) metrics.
 * 
 * <p>Contains TPS averages over different time periods and the current
 * MSPT (milliseconds per tick) average.
 * 
 * @param tps1m TPS average over the last 1 minute
 * @param tps5m TPS average over the last 5 minutes  
 * @param tps15m TPS average over the last 15 minutes
 * @param msptAvg Current MSPT (milliseconds per tick) average
 * 
 * @since 0.0.1
 * @apiNote TPS values are typically capped at 20.0 for optimal performance
 */
public record TpsSnapshot(
    double tps1m,
    double tps5m, 
    double tps15m,
    double msptAvg
) {
    
    /**
     * Creates a new TPS snapshot with validation.
     * 
     * @param tps1m 1-minute TPS average (0.0 to 20.0)
     * @param tps5m 5-minute TPS average (0.0 to 20.0)
     * @param tps15m 15-minute TPS average (0.0 to 20.0)
     * @param msptAvg MSPT average (typically 0.0 to 50.0)
     * @throws IllegalArgumentException if any value is negative
     */
    public TpsSnapshot {
        if (tps1m < 0.0 || tps5m < 0.0 || tps15m < 0.0 || msptAvg < 0.0) {
            throw new IllegalArgumentException("TPS and MSPT values cannot be negative");
        }
    }
    
    /**
     * Gets the overall TPS health status.
     * 
     * @return true if all TPS values are above 19.0
     */
    public boolean isHealthy() {
        return tps1m > 19.0 && tps5m > 19.0 && tps15m > 19.0;
    }
    
    /**
     * Gets the worst (lowest) TPS value across all time periods.
     * 
     * @return the minimum TPS value
     */
    public double getWorstTps() {
        return Math.min(Math.min(tps1m, tps5m), tps15m);
    }
    
    /**
     * Creates a formatted string representation of the TPS data.
     * 
     * @return formatted TPS string
     */
    @Override
    public @NotNull String toString() {
        return String.format("TPS[1m=%.2f, 5m=%.2f, 15m=%.2f, mspt=%.2f]", 
                           tps1m, tps5m, tps15m, msptAvg);
    }
}
