package rinde.logistics.pdptw.mas.route;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

import com.google.common.base.Optional;

/**
 * A {@link RoutePlanner} implementation that lets a vehicle go to its closest
 * destination.
 * @author Rinde van Lon <rinde.vanlon@cs.kuleuven.be>
 */
public class GotoClosestRoutePlanner extends AbstractRoutePlanner {

  Comparator<DefaultParcel> comp;

  private Optional<DefaultParcel> current;
  private final List<DefaultParcel> parcels;

  /**
   * New instance.
   */
  public GotoClosestRoutePlanner() {
    comp = new ClosestDistanceComparator();
    current = Optional.absent();
    parcels = newArrayList();
  }

  @Override
  protected final void doUpdate(Collection<DefaultParcel> onMap, long time) {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    final Collection<DefaultParcel> inCargo = Collections.checkedCollection(
        (Collection) pdpModel.get().getContents(vehicle.get()),
        DefaultParcel.class);
    parcels.clear();
    parcels.addAll(onMap);
    parcels.addAll(onMap);
    parcels.addAll(inCargo);
    updateCurrent();
  }

  private void updateCurrent() {
    if (parcels.isEmpty()) {
      current = Optional.absent();
    } else {
      current = Optional.of(Collections.min(parcels, comp));
    }
  }

  @Override
  protected void nextImpl(long time) {
    if (current.isPresent()) {
      parcels.remove(current.get());
    }
    updateCurrent();
  }

  @Override
  public Optional<DefaultParcel> current() {
    return current;
  }

  @Override
  public boolean hasNext() {
    return !parcels.isEmpty();
  }

  /**
   * @return A {@link SupplierRng} that supplies {@link GotoClosestRoutePlanner}
   *         instances.
   */
  public static SupplierRng<GotoClosestRoutePlanner> supplier() {
    return new DefaultSupplierRng<GotoClosestRoutePlanner>() {
      @Override
      public GotoClosestRoutePlanner get(long seed) {
        return new GotoClosestRoutePlanner();
      }
    };
  }

  static Point getPos(DefaultParcel parcel, PDPModel model) {
    if (model.getParcelState(parcel).isPickedUp()) {
      return parcel.dto.destinationLocation;
    }
    return parcel.dto.pickupLocation;
  }

  class ClosestDistanceComparator implements Comparator<DefaultParcel> {
    @Override
    public int compare(DefaultParcel arg0, DefaultParcel arg1) {
      final Point cur = roadModel.get().getPosition(vehicle.get());
      final Point p0 = getPos(arg0, pdpModel.get());
      final Point p1 = getPos(arg1, pdpModel.get());
      return Double.compare(Point.distance(cur, p0), Point.distance(cur, p1));
    }
  }

}