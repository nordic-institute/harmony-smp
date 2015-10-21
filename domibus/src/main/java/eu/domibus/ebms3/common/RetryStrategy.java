/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.ebms3.common;

import java.util.Date;

/**
 * TODO: add class description
 */
public enum RetryStrategy {

    CONSTANT("CONSTANT", RetryStrategy.ConstantAttemptAlgorithm.ALGORITHM), SEND_ONCE("SEND_ONCE", RetryStrategy.SendOnceAttemptAlgorithm.ALGORITHM);

    private final String name;
    private final RetryStrategy.AttemptAlgorithm algorithm;

    RetryStrategy(String name, RetryStrategy.AttemptAlgorithm attemptAlgorithm) {
        this.name = name;
        this.algorithm = attemptAlgorithm;
    }

    public String getName() {
        return this.name;
    }

    public RetryStrategy.AttemptAlgorithm getAlgorithm() {
        return this.algorithm;
    }


    public enum ConstantAttemptAlgorithm implements RetryStrategy.AttemptAlgorithm {

        ALGORITHM {
            @Override
            public Date compute(Date received, int maxAttempts, int timeoutInMinutes) {
                long now = System.currentTimeMillis();
                long retry = received.getTime();
                long stopTime = received.getTime() + (timeoutInMinutes * 60000) + 5000; // We grant 5 extra seconds to avoid not sending the last attempt
                while (retry <= (stopTime)) {
                    retry += timeoutInMinutes * 60000 / maxAttempts;
                    if (retry > now && retry < stopTime) {
                        return new Date(retry);
                    }
                }
                return null;
            }
        }
    }

    public enum SendOnceAttemptAlgorithm implements RetryStrategy.AttemptAlgorithm {

        ALGORITHM {
            @Override
            public Date compute(Date received, int currentAttempts, int timeoutInMinutes) {

                return null;
            }
        }
    }

    /**
     * NOT FINISHED *
     */
    public interface AttemptAlgorithm {
        Date compute(Date received, int maxAttempts, int timeoutInMinutes);
    }
}
