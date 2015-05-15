package nz.ac.auckland.alm;

import nz.ac.auckland.linsolve.*;
import nz.ac.auckland.linsolve.softconstraints.GroupingSoftSolver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Layout specification.
 */
public class LayoutSpec extends LinearSpec {
    /**
     * The areas that were added to the specification.
     */
    private List<Area> areas = new ArrayList<Area>();

    private List<Row> rows = new ArrayList<Row>();

    private List<Column> columns = new ArrayList<Column>();

    /**
     * X-tab for the left of the GUI.
     */
    private XTab left;
    /**
     * X-tab for the right of the GUI.
     */
    private XTab right;

    /** Y-tab for the top of the GUI. */
    private YTab top;

    /**
     * Y-tab for the bottom of the GUI.
     */
    private YTab bottom;

    /**
     * X-tab for the left border.
     * The difference between the left tab and this tab is the thickness of the border
     */
    private XTab leftInset;
    /**
     * X-tab for the right border.
     * The difference between the right tab and this tab is the thickness of the border
     */
    private XTab rightInset;
    /**
     * Y-tab for the top border.
     * The difference between the top tab and this tab is the thickness of the border
     */
    private YTab topInset;
    /**
     * Y-tab for the bottom border.
     * The difference between the bottom tab and this tab is the thickness of the border
     */
    private YTab bottomInset;

    private Constraint rightConstraint;
    private Constraint bottomConstraint;
    private Constraint leftConstraint;
    private Constraint topConstraint;

    /**
     * Constraints for the insets (borders), if no border is set, the inset tabs will match the left/top/right/bottom tabs.
     */
    private Constraint rightInsetConstraint;
    private Constraint bottomInsetConstraint;
    private Constraint leftInsetConstraint;
    private Constraint topInsetConstraint;

    /** Creates a new, empty layout specification containing only four tabstops left, right, top, bottom for the layout
     * boundaries. */
    public LayoutSpec() {
        //this(new AddingSoftSolver(new KaczmarzSolver()));
        this(new GroupingSoftSolver(new KaczmarzSolver()));
        //this(new LpSolve());
        //this(new AddingSoftSolver(new GaussSeidelSolver(new DeterministicPivotSummandSelector(), 500)));
        //this(new MatlabLinProgSolver());
    }

    /**
     * Constructor for class <code>LayoutSpec</code>.
     *
     * @param solver the Linear Solver that defines the LayoutSpec
     */
    public LayoutSpec(LinearSolver solver) {
        super(solver);

        //Create the Tabs defining the edge of the layout.
        left = new XTab(this);
        left.setName("left");
        top = new YTab(this);
        top.setName("top");
        right = new XTab(this);
        right.setName("right");
        bottom = new YTab(this);
        bottom.setName("bottom");

        //Create the Tabs defining where the borders end.
        leftInset = new XTab(this);
        leftInset.setName("left inset");
        topInset = new YTab(this);
        topInset.setName("top inset");
        rightInset = new XTab(this);
        rightInset.setName("right inset");
        bottomInset = new YTab(this);
        bottomInset.setName("bottom inset");

        //Set Default Constraints
        leftConstraint = addConstraint(1, left, OperatorType.EQ, 0, Penalties.LEFT);
        topConstraint = addConstraint(1, top, OperatorType.EQ, 0, Penalties.TOP);
        leftInsetConstraint = addConstraint(1, leftInset, -1, left, OperatorType.EQ, 0, Penalties.LEFT_INSET);
        topInsetConstraint = addConstraint(1, topInset, -1, top, OperatorType.EQ, 0, Penalties.TOP_INSET);
        rightInsetConstraint = addConstraint(1, right, -1, rightInset, OperatorType.EQ, 0, Penalties.RIGHT_INSET);
        bottomInsetConstraint = addConstraint(1, bottom, -1, bottomInset, OperatorType.EQ, 0, Penalties.BOTTOM_INSET);
    }

    /**
     * Set the X-tab for the right border of the GUI.
     *
     * @param r double which defines the X-tab
     */
    public void setRight(double r) {
        if (rightConstraint == null || !constraints.contains(rightConstraint))
            rightConstraint = addConstraint(1, right, OperatorType.EQ, r);
        else
            rightConstraint.setRightSide(r);
    }

    /**
     * Set the Y-tab for the bottom border of the GUI.
     *
     * @param b double which defines the Y-tab
     */
    public void setBottom(double b) {
        if (bottomConstraint == null || !constraints.contains(bottomConstraint))
            bottomConstraint = addConstraint(1, bottom, OperatorType.EQ, b);
        else
            bottomConstraint.setRightSide(b);
    }

    /**
     * Set the X-tab for the left border of the GUI.
     *
     * @param l double which defines the X-tab
     */
    public void setLeft(double l) {
        leftConstraint.setRightSide(l);
    }

    /**
     * Set the Y-tab for the top border of the GUI.
     *
     * @param t double which defines the X-tab
     */
    public void setTop(double t) {
        topConstraint.setRightSide(t);
    }

    /**
     * Set the X-tab for the right border and the Y-tab for the bottom border of the GUI.
     *
     * @param bot double which defines the Y-tab
     * @param rig double which defines the X-tab
     */
    public void setBottomRight(YTab bot, XTab rig) {
        bottom = bot;
        right = rig;
    }

    /**
     * Sets the constraints which govern the border end tabs.
     *
     * @param left The left borders present on the container managed by ALM.
     * @param top The top borders present on the container managed by ALM.
     * @param right The right borders present on the container managed by ALM.
     * @param bottom The bottom borders present on the container managed by ALM.
     */
    public void setInsetConstraints(int left, int top, int right, int bottom) {
        if (left != leftInsetConstraint.getRightSide()
                || top != topInsetConstraint.getRightSide()
                || right != rightInsetConstraint.getRightSide()
                || bottom != bottomInsetConstraint.getRightSide()) {

            leftInsetConstraint.setRightSide(left);
            topInsetConstraint.setRightSide(top);
            rightInsetConstraint.setRightSide(right);
            bottomInsetConstraint.setRightSide(bottom);

            invalidateLayout();
        }
    }

    /**
     * Sets the constraint which governs the left border end tab.
     *
     * @param left double which defines the X-Tab
     */
    public void setLeftInset(double left) {
        invalidateLayout();
        leftInsetConstraint.setRightSide(left);
    }

    /**
     * Sets the constraint which governs the right border end tab.
     *
     * @param right double which defines the X-Tab
     */
    public void setRightInset(double right) {
        invalidateLayout();
        rightInsetConstraint.setRightSide(right);
    }

    /**
     * Sets the constraint which governs the top border end tab.
     *
     * @param top double which defines the Y-Tab
     */
    public void setTopInset(double top) {
        invalidateLayout();
        topInsetConstraint.setRightSide(top);
    }

    /**
     * Sets the constraint which governs the bottom border end tab.
     *
     * @param bottom double which defines the Y-Tab
     */
    public void setBottomInset(double bottom) {
        invalidateLayout();
        bottomInsetConstraint.setRightSide(bottom);
    }
	
    @Override
    /**
     * Solve the linear equation with LinearSpec.
     */
    public void solve() {
        // if autoPreferredContentSize is set on an area, read just its
        // prefContentSize and penalties settings
        // TODO: is autoPreferredContentSize a good idea?
        for (Area a : areas) {
            if (a.isAutoPreferredContentSize())
                a.setDefaultBehavior();
        }
        super.solve();
    }

    // cached layout values
    // need to be invalidated whenever the layout specification is changed
    Area.Size minSize = Area.UNDEFINED_SIZE;
    Area.Size maxSize = Area.UNDEFINED_SIZE;
    Area.Size preferredSize = Area.UNDEFINED_SIZE;

    /**
     * If the layout is solved previously the cached mininum size,
     * maximum size and preferred size are invalidated;
     * so they need be recalculated when accessing them next time.
     */
    void invalidateLayout() {
        minSize = Area.UNDEFINED_SIZE;
        maxSize = Area.UNDEFINED_SIZE;
        preferredSize = Area.UNDEFINED_SIZE;
    }

    /**
     * Get the cached minimal size of the GUI, if there was none it will be calculated.
     * To invalidate the cache use invalidateLayout().
     *
     * @return Size defining the minimal size of the GUI
     */
    public Area.Size getMinSize() {
        if (minSize == Area.UNDEFINED_SIZE)
            minSize = calculateMinSize();
        return minSize;
    }

    /**
     * Get the cached maximal size of the GUI, if there was none it will be calculated.
     * To invalidate the cache use invalidateLayout().
     *
     * @return Size defining the maximal size of the GUI
     */
    public Area.Size getMaxSize() {
        if (maxSize == Area.UNDEFINED_SIZE)
            maxSize = calculateMaxSize();
        return maxSize;
    }

    /**
     * Get the cached preferred size of the GUI, if there was none it will be calculated.
     * To invalidate the cache use invalidateLayout().
     *
     * @return Size defining the preferred size of the GUI
     */
    public Area.Size getPreferredSize() {
        if (preferredSize == Area.UNDEFINED_SIZE)
            preferredSize = calculatePreferredSize();
        return preferredSize;
    }

    /**
     * Calculate the minimal size of the GUI.
     * If the specifications have not changed use getMinSize to get an
     * cached value for the minimal size and save some CPU cycles.
     *
     * @return Size defining the minimal size of the GUI
     */
    private Area.Size calculateMinSize() {

        //Store the preferred sizes and temporarily set the preferred size to the min size.
        Area.Size[] store = new Area.Size[areas.size()];

        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            store[i] = a.getPreferredContentSize();
            a.setPreferredContentSize(a.getMinContentSize());
        }
        //Calculate the preferred container size with the min sizes set as preferred sizes.
        Area.Size min = calculatePreferredSize();

        //Restore the original preferredSizeValues.
        for (int i = 0; i < areas.size(); i++) {
            Area a = areas.get(i);
            a.setPreferredContentSize(store[i]);
        }

        //Recalculate to avoid any potential errors.
        solve();

        return min;
    }

    /**
     * Calculate the maximal size of the GUI.
     * If the specifications have not changed use getMaxSize to get an cached
     * value for the maximal size and save some CPU cycles.
     *
     * @return Size defining the maximal size of the GUI
     */
    private Area.Size calculateMaxSize() {
        /*
		 * List<Summand> buf = getObjFunctionSummands();
		 * setObjFunctionSummands(new ArrayList<Summand>());
		 * addObjFunctionSummand(-1, right); addObjFunctionSummand(-1, bottom);
		 * solveLayout(); setObjFunctionSummands(buf); updateObjFunction();
		 */

        return new Area.Size(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Calculate the preferred size of the GUI.
     * If the specifications have not changed use getPreferredSize to get an cached value
     * for the preferred size and save some CPU cycles.
     * TODO - It returns the current size.
     *
     * @return Size defining the preferred size of the GUI
     */
    private Area.Size calculatePreferredSize() {
        //Store the current constraint values and reset GUI edge tabs and constraints to default.
        double leftValue = leftConstraint.getRightSide();
        double topValue = topConstraint.getRightSide();
        double rightValue = Double.NaN;
        double bottomValue = Double.NaN;
        right.setValue(Double.NaN);
        bottom.setValue(Double.NaN);
        //Set top/left values to default
        setLeft(0);
        setTop(0);
        //Remove the constraints from the specification once the values are stored.
        if (rightConstraint != null) {
            rightValue = rightConstraint.getRightSide();
            removeConstraint(rightConstraint);
            rightConstraint = null;

        }

        if (bottomConstraint != null) {
            bottomValue = bottomConstraint.getRightSide();
            removeConstraint(bottomConstraint);
            bottomConstraint = null;

        }

        //Solve with no fixed GUI size
        solve();

        //Resulting value for left/right is the preferred size of the GUI
        Area.Size prefSize = new Area.Size(0, 0);
        prefSize.width = (int) Math.round(right.getValue() - left.getValue());
        prefSize.height = (int) Math.round(bottom.getValue() - top.getValue());


        //reset the values to the state they were in before this method was called
        setLeft(leftValue);
        setTop(topValue);
        //Will execute with everything except NaN
        if (rightValue == rightValue) {
            setRight(rightValue);
        }
        if (bottomValue == bottomValue) {
            setBottom(bottomValue);
        }
        //solve again to restore specification to prior state.
        solve();

        return prefSize;
    }

    /**
     * Adds a new x-tab to the specification.
     *
     * @return the new x-tab
     */
    public XTab addXTab() {
        return new XTab(this);
    }

    /**
     * Adds a new x-tab to the specification.
     *
     * @param name String define the name of resulting x-tab
     * @return the new x-tab
     */
    public XTab addXTab(String name) {
        XTab x = new XTab(this);
        x.setName(name);
        return x;
    }

    /**
     * Adds a new y-tab to the specification.
     *
     * @return the new y-tab
     */
    public YTab addYTab() {
        return new YTab(this);
    }

    /**
     * Adds a new y-tab to the specification.
     *
     * @param name String define the name of resulting y-tab
     * @return the new y-tab
     */
    public YTab addYTab(String name) {
        YTab y = new YTab(this);
        y.setName(name);
        return y;
    }

    /**
     * Adds a new row to the specification.
     *
     * @return the new row
     */
    public Row addRow() {
        Row r = new Row(this);
        rows.add(r);
        return r;
    }

    /**
     * Add a row by presenting existing tabs.
     *
     * @param x the YTab
     * @param y the YTab
     * @return the new Row
     */
    public Row addRowWithReuseTabs(YTab x, YTab y) {
        Row r = new Row(this, x, y);
        rows.add(r);
        return r;
    }

    /**
     * Add a row by presenting existing tabs to the specified index.
     *
     * @param i the target index
     * @param x the YTab
     * @param y the YTab
     * @return the new Row
     */
    public Row addRowWithReuseTabs(int i, YTab x, YTab y) {
        Row r = new Row(this, x, y);
        rows.add(i, r);
        return r;
    }

    /**
     * Adds a new row to the specification at an index.
     *
     * @param index
     * @return the new row
     */
    public Row addRow(int index) {
        Row r = new Row(this);
        rows.add(index, r);
        return r;
    }

    /**
     * Adds a new row to the specification that is glued to the given y-tabs.
     *
     * @param top
     * @param bottom
     * @return the new row
     */
    public Row addRow(YTab top, YTab bottom) {
        Row row = new Row(this);
        if (top != null)
            row.constraints.add(row.getTop().isEqual(top));
        if (bottom != null)
            row.constraints.add(row.getBottom().isEqual(bottom));
        rows.add(row);
        return row;
    }

    /**
     * Adds a new column to the specification.
     *
     * @return the new column
     */
    public Column addColumn() {
        Column c = new Column(this);
        columns.add(c);
        return c;
    }

    /**
     * Adds a new column to the specification at an index.
     *
     * @return the new column
     */
    public Column addColumn(int index) {
        Column c = new Column(this);
        columns.add(index, c);
        return c;
    }

    /**
     * Adds a new column to the specification that is glued to the given x-tabs.
     *
     * @param left
     * @param right
     * @return the new column
     */
    public Column addColumn(XTab left, XTab right) {
        Column column = new Column(this);
        if (left != null)
            column.constraints.add(column.getLeft().isEqual(left));
        if (right != null)
            column.constraints.add(column.getRight().isEqual(right));
        columns.add(column);
        return column;
    }

    /**
     * Adds a new area to the specification, setting only the necessary minimum
     * size constraints.
     *
     * @param left           left border
     * @param top            top border
     * @param right          right border
     * @param bottom         bottom border
     * @param minContentSize minimum content size
     * @return the new area
     */
    public Area addArea(XTab left, YTab top, XTab right, YTab bottom, Area.Size minContentSize) {

        invalidateLayout();
        Area a = new Area(this, left, top, right, bottom, minContentSize);
        areas.add(a);
        return a;
    }

    /**
     * Adds a new area to the specification, setting only the necessary minimum
     * size constraints.
     *
     * @param row            the row that defines the top and bottom border
     * @param column         the column that defines the left and right border
     * @param minContentSize minimum content size
     * @return the new area
     */
    public Area addArea(Row row, Column column, Area.Size minContentSize) {

        invalidateLayout();
        Area a = new Area(this, row, column, minContentSize);
        areas.add(a);
        return a;
    }

    /**
     * Adds a new area to the specification, automatically setting preferred
     * size constraints.
     *
     * @param left    left border
     * @param top     top border
     * @param right   right border
     * @param bottom  bottom border
     * @return the new area
     */
    public Area addArea(XTab left, YTab top, XTab right, YTab bottom) {
        invalidateLayout();
        Area a = new Area(this, left, top, right, bottom, new Area.Size(0, 0));

        a.setDefaultBehavior();
        areas.add(a);
        return a;
    }

    /**
     * Adds a new area to the specification, automatically setting preferred
     * size constraints.
     *
     * @param row     the row that defines the top and bottom border
     * @param column  the column that defines the left and right border
     * @return the new area
     */
    public Area addArea(Row row, Column column) {
        invalidateLayout();
        Area a = new Area(this, row, column, new Area.Size(0, 0));
        a.setDefaultBehavior();
        areas.add(a);
        return a;
    }

    /**
     * Adds a new area to the specification, automatically setting preferred
     * size constraints.
     *
     * @param row     the row that defines the top and bottom border
     * @param column  the column that defines the left and right border
     * @return the new area
     */
    public Area addArea(Row row, Column column, boolean defaultBehaviour) {
        invalidateLayout();
        Area a = new Area(this, row, column, new Area.Size(0, 0));
        if (defaultBehaviour) {
            a.setDefaultBehavior();
        }
        areas.add(a);
        return a;
    }

    /**
     * Write the spec into XML
     *
     * @param out the output stream writer to use
     */
    void writeXML(OutputStreamWriter out) {
        for (Area a : areas) {
            a.writeXML(out);
        }
        for (Constraint c : getConstraints()) {
            if (c.Owner == null)
                c.writeXML(out);
        }
    }

    /**
     * Write the given summands into XML
     *
     * @param out      the output stream writer to use
     * @param summands the summands to be written out
     */
    void writeXMLSummand(OutputStreamWriter out, Summand[] summands) {
        try {
            for (Summand s : summands) {
                out.write("\t\t\t<summand>\n");
                out.write("\t\t\t\t<coeff>" + s.getCoeff() + "</coeff>\n");
                out.write("\t\t\t\t<var>" + s.getVar() + "</var>\n");
                out.write("\t\t\t</summand>\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the areas that were added to the specification.
     *
     * @return the areas that were added to the specification.
     */
    public List<Area> getAreas() {
        return areas;
    }

    /**
     * Get the rows that were added to the specification.
     *
     * @return the rows that were added to the specification.
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Get the columns that were added to the specification.
     *
     * @return the columns that were added to the specification.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Get the X-tab for the left of the GUI.
     *
     * @return the x-tab.
     */
    public XTab getLeft() {
        return left;
    }

    /**
     * Get the X-tab for the right of the GUI.
     *
     * @return the x-tab.
     */
    public XTab getRight() {
        return right;
    }

    /**
     * Get the Y-tab for the top of the GUI.
     *
     * @return the y-tab.
     */
    public YTab getTop() {
        return top;
    }

    /**
     * Get the Y-tab for the bottom of the GUI.
     *
     * @return the y-tab.
     */
    public YTab getBottom() {
        return bottom;
    }

    /**
     * Get the X-tab for the left border of the GUI.
     *
     * @return the x-tab.
     */
    public XTab getLeftInset() {
        return leftInset;
    }

    /**
     * Get the X-tab for the right border of the GUI.
     *
     * @return the x-tab.
     */
    public XTab getRightInset() {
        return rightInset;
    }

    /**
     * Get the Y-tab for the top border of the GUI.
     *
     * @return the y-tab.
     */
    public YTab getTopInset() {
        return topInset;
    }

    /**
     * Get the Y-tab for the top border of the GUI.
     *
     * @return the y-tab.
     */
    public YTab getBottomInset() {
        return bottomInset;
    }

    /**
     * Set the minimal size of the GUI
     *
     * @param minSize defining the minimal size of the GUI
     */
    public void setMinSize(Area.Size minSize) {
        this.minSize = minSize;
    }


    /**
     * Set the maximal size of the GUI
     *
     * @param maxSize defining the maximal size of the GUI
     */
    public void setMaxSize(Area.Size maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Set the preferred size of the GUI
     *
     * @param preferredSize defining the preferred size of the GUI
     */
    public void setPreferredSize(Area.Size preferredSize) {
        this.preferredSize = preferredSize;
    }
}
