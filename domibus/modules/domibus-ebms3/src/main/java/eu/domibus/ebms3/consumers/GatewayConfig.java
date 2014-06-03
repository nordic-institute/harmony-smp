package eu.domibus.ebms3.consumers;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "gateway")
public class GatewayConfig {
    @ElementList(inline = true)
    protected List<Consumption> consumptions = new ArrayList<Consumption>();

    public List<Consumption> getConsumptions() {
        return consumptions;
    }

    public void setConsumptions(final List<Consumption> consumptions) {
        this.consumptions = consumptions;
    }

    public void addConsumption(final Consumption consumption) {
        if (consumption == null) {
            return;
        }
        consumptions.add(consumption);
    }

    public Consumption getMatchingConsumption() {
        if (consumptions == null || consumptions.size() == 0) {
            return null;
        }
        for (int i = 0; i < consumptions.size(); i++) {
            final Consumption c = consumptions.get(i);
            if (c.matchesCurrentMessageContext()) {
                return c;
            }
        }
        return null;
    }
}