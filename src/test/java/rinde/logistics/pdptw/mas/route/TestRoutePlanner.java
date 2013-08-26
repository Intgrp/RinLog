/**
 * 
 */
package rinde.logistics.pdptw.mas.route;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import rinde.sim.pdptw.common.DefaultParcel;

import com.google.common.base.Optional;

/**
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 * 
 */
public class TestRoutePlanner extends AbstractRoutePlanner {

  protected final Queue<DefaultParcel> route;

  public TestRoutePlanner() {
    route = newLinkedList();
  }

  public Optional<DefaultParcel> current() {
    return Optional.fromNullable(route.peek());
  }

  public boolean hasNext() {
    return !route.isEmpty();
  }

  @Override
  protected void doUpdate(Collection<DefaultParcel> onMap, long time) {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    final Collection<DefaultParcel> inCargo = Collections
        .checkedCollection((Collection) pdpModel.get().getContents(vehicle
            .get()), DefaultParcel.class);
    route.clear();
    route.addAll(onMap);
    route.addAll(inCargo);
    route.addAll(onMap);
  }

  @Override
  protected void nextImpl(long time) {
    route.poll();
  }

}
