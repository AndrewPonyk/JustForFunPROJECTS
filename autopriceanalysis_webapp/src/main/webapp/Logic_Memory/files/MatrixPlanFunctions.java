package com.agileassetsinc.main;

import com.agileassetsinc.core.*;
import com.agileassetsinc.core.cache.SetupColumnIdCache;
import com.agileassetsinc.core.db.DbIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: tonya
 * Date: Nov 24, 2006
 * Time: 12:14:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixPlanFunctions {
    private DataLayer dl;
    private Integer planId;
    private Integer fiscalYear;
    private DataStore dsPlan;
    private DataStore dsTotals;
    private boolean accessRegPlan;
    private double hoursPerDay = 8;
    private boolean restrictZeros = false;
    private int relativeCalendarImportance;
    private Integer columnScale;
    
    
    public MatrixPlanFunctions(DataLayer dl, Integer planId, Integer fiscalYear, DataStore dsPlan, DataStore dsTotals, boolean accReg) throws TRDIException
    {
    	this.dl = dl;
        this.planId = planId;
        this.fiscalYear = fiscalYear;
        this.dsPlan = dsPlan;
        this.dsTotals = dsTotals;
        accessRegPlan = accReg;

        DataStore ds = dl.createRetrieveDataStore("select CAL_HOURS_PER_DAY from SETUP_CUSTOM_CALENDAR where CALENDAR_ID=1");
        if (ds.rowCount() == 1 && ds.getItemNumber(1,1) > 0)
        	hoursPerDay=ds.getItemNumber(1,1);
    }
    
    
    public void CopyPMSPlan(boolean overwrite,Integer MWPType ) throws TRDIException
    {
        String sql_str="select "+planId+"as plan_id,"+fiscalYear+" as EFF_FISCAL_YEAR, 2 as asset_type_id,a.activity_id,a.comment_str,a.total_cost,a.inv_elem_id,a.owner_id,\n" +
                "  b.unit_cost  AS rate ,\n" +
                "  b.MAN_HOURS as unit_hours,\n" +
                "  b.NUM_MEN,\n" +
                "\tround(case when b.unit_cost is null or b.unit_cost=0 then null else a.total_cost/\t\t  b.unit_cost end,2) as amount    \n" +
                "    from (\n" +
                " select  a.plan_year,a.PMS_TREATMENT_ID,c.activity_id,b.route_id,\n" +
                " greatest(b.lane_dir,lb.lane_dir) as lane_dir,b.lane_id,\n" +
                " greatest(b.offset_from,lb.offset_from) as offset_from ,\n" +
                " 'Route: '||(Select sr.route_name from setup_route sr where sr.route_id=b.route_id)||'; Dir:'||(select sl.lane_dir_name from setup_lane_dir sl where sl.lane_dir=greatest(b.lane_dir,lb.lane_dir))\n" +
                " ||'; from '||to_char(greatest(b.offset_from,lb.offset_from))||' to '||TO_CHAR(least(b.offset_to, lb.offset_to)) as comment_str,\n" +
                " a.project_price * \n" +
                " case when b.lane_dir=0 and lb.lane_dir>0 then 0.5 else 1 end *\n" +
                " case when b.offset_to > b.offset_from then (least(b.offset_to, lb.offset_to) - greatest(b.offset_from,lb.offset_from) )/(b.offset_to - b.offset_from) else 1 end  as total_cost,\n" +
                " least(b.offset_to, lb.offset_to) as offset_to,\n" +
                "  \n" +
                "  i.section_id  as inv_elem_id,\n" +
                "  i.crew_id as owner_id\n" +
                " from pms_master_wp a,setup_loc_ident b,setup_activity_tcplan c,setup_loc_ident lb,section_inventory i  where \n" +
                " a.PMS_MASTER_WP_TYPE_ID="+MWPType+" and a.approved=1 and a.plan_year="+fiscalYear+" and \n" +
                " a.loc_ident=b.loc_ident and b.sourse_table='PMS_MASTER_WP' and c.PMS_TREATMENT_ID=a.PMS_TREATMENT_ID\n" +
                " and lb.sourse_table='SECTION_INVENTORY'  \n" +
                "and b.route_id=lb.route_id and (b.lane_dir=lb.lane_dir or b.lane_dir=0 or lb.lane_dir =0)\n" +
                "and b.offset_from<lb.offset_to and b.offset_to>lb.offset_from and i.loc_ident=lb.loc_ident and section_status_id=1 and lb.offset_from<lb.offset_to) a,\n" +
                "qg_owner_ref_year b \n" +
                "WHERE b.eff_fiscal_year = "+fiscalYear+" \n" +
                "                            AND b.owner_id = a.owner_id\n" +
                "                           AND b.activity_id = a.activity_id";

        DataStore ds_source= dl.createDataStore(sql_str) ;
        ds_source.reset();
        ds_source.retrieve( );
//        for (int i=1;i<=ds_source.rowCount();i++)  {
//            ds_source.setItemNumber(i,"PLAN_ID",PlanId);
//            ds_source.setItemNumber(i,"EFF_FISCAL_YEAR",FiscalYear);
//
//        }
        if (overwrite) dsPlan.deleteAllRows();
        dsPlan.insertRows(ds_source);
        if (!overwrite) aggregateRows();
        for (int i=1;i<=dsPlan.rowCount();i++)  distributeMenHours(i);

    }
    
    
    public void CopyPlan(boolean overwrite,Integer sourcePlan,Integer sourceFiscalYear) throws TRDIException
    {
    	
        String sql_str=dsPlan.getSql();
        if (sourcePlan==0)         sql_str=sql_str.replace("MASTER_PLAN_MATRIX","ACCOMPL_PLAN_MATRIX") ;
        DataStore ds_source= dl.createDataStore(sql_str) ;
        ds_source.reset();
        ds_source.retrieve(sourcePlan,sourceFiscalYear);
        for (int i=1;i<=ds_source.rowCount();i++)  {
            ds_source.setItemNumber(i,"PLAN_ID",planId);
            ds_source.setItemNumber(i,"EFF_FISCAL_YEAR",fiscalYear);

        }
        if (overwrite) dsPlan.deleteAllRows();
        dsPlan.insertRows(ds_source);
        if (!overwrite) aggregateRows();


    }


    public void aggregateRows() throws TRDIException
    {
        DbIndex matrixUniqueIndex = dl.getDbIndex("MASTER_PLAN_MATRIX_UI");
        String index_columns[]= matrixUniqueIndex.getCols();
        String non_index_columns[]= {"FTE_1","FTE_2","FTE_3","FTE_4","FTE_5","FTE_6","FTE_7","FTE_8","FTE_9","FTE_10","FTE_11","FTE_12","TOTAL_FTE","CALC_FTE","DELTA_FTE","TOTAL_COST"};

        dsPlan.sort(dl, TRDIUtils.join(Arrays.asList(index_columns), ","));
        for (int i=2;i<=dsPlan.rowCount();i++)    {
        boolean combine=true;
            for (String ind_column:index_columns)
            combine=combine && TRDIUtils.areObjectsEqual( dsPlan.getItemInteger(i,ind_column),dsPlan.getItemInteger(i-1,ind_column));
            if (!combine) continue;
            for (String column:non_index_columns) {
                if (dsPlan.getItemNumber(i-1,column)==null)dsPlan.setItemNumber(i-1,column,dsPlan.getItemNumber(i,column))  ;
                else if (dsPlan.getItemNumber(i,column)!=null)
             dsPlan.setItemNumber(i-1,column,dsPlan.getItemNumber(i-1,column)+dsPlan.getItemNumber(i,column));
            }
            if (dsPlan.isValidColumn("COMMENT_STR"))  dsPlan.setItemString(i-1,"COMMENT_STR",(dsPlan.isItemNull(i-1,"COMMENT_STR") ? "" : dsPlan.getItemString(i-1,"COMMENT_STR"))+(dsPlan.isItemNull(i,"COMMENT_STR") ? "" : dsPlan.getItemString(i,"COMMENT_STR")));
            dsPlan.deleteRow(i);
            i--;

        }

    }
    
    
    /**
     * Distributes the total monthly hours for this activity.  The result of this is that the total hours are
     * normalized across all months to still sum up to the total activity amount, but the hours are redistributed
     * across the months to follow the same distribution as the equipment (or activity) calendar.
     * @param rows The selected rows in the planned activity that we are attempting to normalize.
     * @throws TRDIException
     */
    public void distributeMenHours(int[] rows) throws TRDIException
    {
	    for (int i = 0; i < rows.length; i++)
	    {
	    	distributeMenHours(rows[i]);
	    }
    }
    
    
    /**
     * Distributes the total monthly hours for this activity.  The result of this is that the total hours are
     * normalized across all months to still sum up to the total activity amount, but the hours are redistributed
     * across the months to follow the same distribution as the equipment (or activity) calendar.
     * @param row The selected row in the planned activity that we are attempting to normalize.
     * @throws TRDIException
     */
    public void distributeMenHours(int currRow) throws TRDIException
    {
    	if (dsPlan.getItemNumber(currRow,"AMOUNT") == null || dsPlan.getItemNumber(currRow,"UNIT_HOURS") == null)
    	{
    		throw new TRDIException(949);	// cannot distribute hours because either Amount or Unit dur is null
    	}
    	
    	double[] coeff = getCalendarCoefficients(dsPlan.getItemInteger(currRow, "OWNER_ID"), dsPlan.getItemInteger(currRow, "ACTIVITY_ID"));
    	
    	// get the appropriate scale (defined by scale of TOTAL_FTE column)
    	int precision = getScale();
    	
		Double unitHours = dsPlan.getItemNumber(currRow, "UNIT_HOURS");
		Double totalCost = dsPlan.getItemNumber(currRow, "TOTAL_COST");
		Double rate = dsPlan.getItemNumber(currRow, "RATE");
		double totalHours = unitHours * totalCost / rate / hoursPerDay;
		totalHours = TRDIUtils.roundNumber(totalHours, precision);
    	double activityManHours = TRDIUtils.roundNumber(totalHours * Math.pow(10, precision), 0);
    	
    	DataStore dsReq = prepareSolverDS("USER_ANALYSIS_SOLVER_REQ");
    	DataStore dsMain = prepareSolverDS("USER_ANALYSIS_SOLVER_MAIN");
    	DataStore dsSolver = prepareSolverDS("USER_ANALYSIS_SOLVER_LVL");
    	DataStore dsCol = prepareSolverDS("USER_ANALYSIS_SOLVER_COL");
    	DataStore dsBnd = prepareSolverDS("USER_ANALYSIS_SOLVER_BND");
    	String userId = dl.getSession().getUserId();
    	
    	// insert the obj function row
    	int row = dsMain.insertRow(0);
    	dsMain.setItemString(row, "USER_ID", userId);
    	dsMain.setItemString(row, "CONST_SIGN", "N");
    	dsMain.setItemString(row, "CONSTR_NAME", "AAA");
    	dsMain.setItemNumber(row, "IS_MAX", 0);
    	
    	// insert the K-type constraint (planned activity for the year, for this one activity)
    	row = dsMain.insertRow(0);
    	dsMain.setItemString(row, "USER_ID", userId);
    	dsMain.setItemString(row, "CONST_SIGN", "E");
    	dsMain.setItemString(row, "CONSTR_NAME", "K1");
    	dsMain.setItemNumber(row, "RHS", activityManHours);
    	dsMain.setItemNumber(row, "IS_MAX", 0);
    	
    	// insert each month for this row into USER_ANALYSIS_SOLVER_LVL so we can retrieve solutions
    	for (int i = 1; i <= 12; i++)
    	{
    		dsSolver.insertRows(dsPlan, currRow, currRow);
    		dsSolver.setItemNumber(dsSolver.rowCount(), "EFF_FISCAL_MONTH", i);
    		dsSolver.setItemNumber(dsSolver.rowCount(), "PLAN_ROW", currRow);
    	}
    	
    	// number the records
    	for (int i = 1; i <= dsSolver.rowCount(); i++)
    	{
    		dsSolver.setItemString(i, "USER_ID", userId);
    		dsSolver.setItemNumber(i, "REC_NUM", i);
    	}
    	
    	
    	// insert all constraints, signs, and bounds
    	String varNum, paddedNum, X, Y, YH, YL;
    	int padLength = 5;
    	
    	for (int i = 1; i <= 12; i++)
    	{
    		varNum = "" + i;
    		paddedNum = TRDIUtils.leftPad(varNum, padLength, '0');
    		X = "X" + paddedNum;
    		Y = "Y" + paddedNum;
    		YH = "YH" + varNum;
    		YL = "YL" + varNum;
    		
    		insertDsCol(dsCol, X, "K1", 1);
    		
    		// Insert YH and YL constraints so the problem becomes linear (see levelFTE() for more info)
    		insertDsCol(dsCol, X, YH, -1);
    		insertDsCol(dsCol, X, YL, 1);
    		
    		insertDsCol(dsCol, Y, "AAA", 1);
    		insertDsCol(dsCol, Y, YH, 1);
    		insertDsCol(dsCol, Y, YL, 1);
    		
    		// insert RHS for YH/YL constraints
    		insertDsReq(dsReq, "G", YH, -1*coeff[i-1]*activityManHours);
    		insertDsReq(dsReq, "G", YL, coeff[i-1]*activityManHours);
    		
    		// insert bound for X
    		insertBound(dsBnd, coeff[i-1], X);
    	}
    	
    	dsReq.insertRows(dsMain);
    	dl.saveData(dsSolver, dsMain, dsReq, dsCol, dsBnd);
    	
    	// update the objective row in USER_ANALYSIS_SOLVER_MAIN so that maxNode is set high enough
    	dl.executeSQL("UPDATE USER_ANALYSIS_SOLVER_MAIN \n" +
    					"SET MAX_NODE=(SELECT 2 * COUNT(1) FROM USER_ANALYSIS_SOLVER_LVL WHERE USER_ID=get_user_id()) \n" +
    					"WHERE USER_ID=get_user_id() AND CONST_SIGN='N'");
    	
    	// with all constraints in the table we want to create our MPS file and call solver
    	CbcSolverWrapper.execute(dl, false, "USER_ANALYSIS_SOLVER_LVL");
    	
    	// zero out all of the current values in the row
    	for (int i = 1; i <= 12; i++)
    	{
    		dsPlan.setItemNumber(currRow, "FTE_"+i, 0);
    	}
    	
    	// get solutions back from solver and put them into the appropriate tables to update values in the window
    	dsSolver.retrieve();
    	for (int i = 1; i <= dsSolver.rowCount(); i++)
    	{
    		String colName = "FTE_" + dsSolver.getItemInteger(i, "EFF_FISCAL_MONTH");
    		Double solution = dsSolver.getItemNumber(i, "SOLUTION") / Math.pow(10, precision);
    		dsPlan.setItemNumber(currRow, colName, solution);
    	}
    	

    	dsPlan.setItemNumber(currRow, "TOTAL_FTE", totalHours);
    	dsPlan.setItemNumber(currRow, "DELTA_FTE", 0);
    }
    
    
    /**
     * Returns an array of coefficients representing either the equipment schedule, or activity calendar.  As a
     * side effect, this method will set the instance variable restrictZeros to true or false, depending on
     * whether or not the zeros in the returned array are hard constraints to solver.
     * @param ownerId The owner id for the current row in the plan (aggregated plan if we are leveling).
     * @param activityId The activity id for the current row in the plan (aggregated plan if we are leveling).
     * @return An array of coefficients for 12 months.
     * @throws TRDIException
     */
    private double[] getCalendarCoefficients(int ownerId, int activityId) throws TRDIException
    {
    	/* First we need to check for EQUIPMENT_ACTIVITY_SCHEDULE table.  If it exists then we should pull values
    	 * from it.  The getEquipmentCoefficients() method should be updated to no longer use connect by prior,
    	 * and it will be nice if it simply returns an array of 12 values.  If it has a zero for any month this
    	 * should be a hard constraint that absolutely must be followed.  We need to figure out how to map such
    	 * a constraint to an X value (is it simply 12*row + monthNumber?).  Also need to figure out where to
    	 * store such constraints so that they will ultimately be written to the mps file.
    	 * 
    	 * If there is no EQUIPMENT_ACTIVITY_SCHEDULE table or the returned array is empty then we should take
    	 * the activity calendar into consideration.  Here we should have a method that returns an array of 12
    	 * values.  It would be nice to also have it return a boolean (so return Object[] ???).  The array will
    	 * define the share for each month and the boolean will define if the user has checked off that all zeros
    	 * must be restricted or not (this is their option when defining the calendar).  As part of this, we need
    	 * to modify QG_OWNER_ACT_CALENDAR_VW to return whether or not the user is restricting zeros.
    	 * 
    	 * Finally, if neither of the above is defined then we simply have a uniform activity calendar.  In this
    	 * case we would want to simply fill the array with 1/12 for each month.
    	 */
    	double[] ret = null;
    	setRestrictZeros(false);	// default to false for each call
    	if (dl.doesTableExist("EQUIPMENT_ACTIVITY_SCHEDULE"))
    	{
    		ret = getEquipmentCoefficients(ownerId, activityId);
    	}
    	
    	// if we didn't have an equipment schedule then fall back on activity calendar or a uniform calendar
    	if (ret == null)
    	{
    		ret = getActivityCalendarCoefficients(ownerId, activityId);
    		if (ret == null)
    		{
    			setRestrictZeros(false);
        		setRelativeCalendarImportance(1);
    			ret = new double[12];
    			for (int i = 0; i < 12; i++)
    			{
    				ret[i] = 1.0 / 12.0;
    			}
    		}
    	}
    	
    	return ret;
    }
    
    
    /**
     * Retrieves the monthly equipment coefficients for the given plan row.  As a side effect, if this
     * method returns an array then it will also set the instance variable restrictZeros to true, to
     * indicate that the zeros in the returned array are hard constraints to solver.
     * @param ownerId The owner id for the current row in the plan (aggregated plan if we are leveling).
     * @param activityId The activity id for the current row in the plan (aggregated plan if we are leveling).
     * @return An array of coefficients for 12 months or null if there is no equipment schedule.
     * @throws TRDIException
     */
    private double[] getEquipmentCoefficients(int ownerId, int activityId) throws TRDIException
    {
    	double[] ret = null;
    	DataStore dsEquipCoeff = null;
    	DataStore dsCheck = dl.createDataStore("SELECT COUNT (1) \n" +
                    " FROM plan_activity_equipment a,EQUIPMENT_SCHEDULE_OWNER c \n" +
                    " WHERE a.activity_id = ? \n" +
                    " AND a.owner_id=c.district_id and c.in_subdistrict_id=?");
		dsCheck.retrieve(activityId, ownerId);
		
		
		// if we have at least one row then there are equipment coefficients to be set
		if (dsCheck.getItemInteger(1,1) > 0)
		{
			dsEquipCoeff = dl.createDataStore("SELECT   c.eff_fiscal_month,\n" +
					"           SUM (b.eff_hours)\n" +
					"         / AVG ((SELECT SUM (b.eff_hours) \n" +
					"                   FROM setup_month_year a, setup_custom_calendar_det b\n" +
					"                  WHERE a.calendar_id = 1\n" +
					"                    AND a.eff_fiscal_year = ?\n" +
					"                    AND a.eff_fiscal_month = c.eff_fiscal_month\n" +
					"                    AND a.eff_year = TO_NUMBER (TO_CHAR (b.eff_date, 'YYYY'))\n" +
					"                    AND a.eff_month = TO_NUMBER (TO_CHAR (b.eff_date, 'MM')))\n" +
					"               ) AS month_coef\n" +
					"    FROM equipment_activity_schedule a,\n" +
					"         plan_activity_equipment b,\n" +
					"         setup_custom_calendar_det b,\n" +
					"         setup_month_year c\n" +
					"   WHERE a.in_subdistrict_id = ?\n" +
					"     AND   a.equipment_id = b.equipment_id\n" +
					"     AND a.district_id = b.owner_id\n" +
					"     AND b.activity_id = ?\n"+
					"     AND b.calendar_id = 1\n" +
					"     AND b.eff_date BETWEEN a.start_date AND a.end_date\n" +
					"     AND TO_NUMBER (TO_CHAR (b.eff_date, 'YYYY')) = c.eff_year\n" +
					"     AND TO_NUMBER (TO_CHAR (b.eff_date, 'MM')) = c.eff_month\n" +
					"     AND c.eff_fiscal_year = ?\n" +
					"GROUP BY c.eff_fiscal_month");
			dsEquipCoeff.retrieve(fiscalYear, ownerId, activityId, fiscalYear);
			
			// set 0 as default and then set the values for the months that have data
			setRestrictZeros(true);
			ret = new double[12];
			for (int i = 0; i < ret.length; i++)
			{
				ret[i] = 0.0;
			}
			
			for (int i = 1; i <= dsEquipCoeff.rowCount(); i++)
			{
				int pos = dsEquipCoeff.getItemInteger(i, "EFF_FISCAL_MONTH") - 1;
				ret[pos] = dsEquipCoeff.getItemNumber(i, "MONTH_COEF");
			}
		}
		
		return ret;
    }
    
    
    /**
     * Retrieve the activity calendar coefficients for the given owner and activity.  As a side effect, this
     * method will set the instance variable restrictZeros to true or false, depending on whether or not the
     * zeros in the returned array are hard constraints to solver.
     * @param ownerId The owner_id to evaluate
     * @param activityId The activity_id to evaluate
     * @return An array of coefficients for 12 months or null if there is no activity calendar.
     * @throws TRDIException
     */
    private double[] getActivityCalendarCoefficients(int ownerId, int activityId) throws TRDIException
    {
    	double[] ret = null;
    	
    	// if we have an equipment activity schedule then we need the monthly coefficients by activity and owner
    	DataStore dsCalendar = dl.createDataStore("select b.eff_fiscal_month as month_id, coalesce(\n" +
    			"case \n" +
    			"when b.eff_fiscal_month=1 then a.MONTH_1 \n" +
    			"when b.eff_fiscal_month=2 then a.MONTH_2 \n" +
    			"when b.eff_fiscal_month=3 then a.MONTH_3 \n" +
    			"when b.eff_fiscal_month=4 then a.MONTH_4 \n" +
    			"when b.eff_fiscal_month=5 then a.MONTH_5 \n" +
    			"when b.eff_fiscal_month=6 then a.MONTH_6 \n" +
    			"when b.eff_fiscal_month=7 then a.MONTH_7 \n" +
    			"when b.eff_fiscal_month=8 then a.MONTH_8 \n" +
    			"when b.eff_fiscal_month=9 then a.MONTH_9 \n" +
    			"when b.eff_fiscal_month=10 then a.MONTH_10\n" +
    			"when b.eff_fiscal_month=11 then a.MONTH_11 \n" +
    			"when b.eff_fiscal_month=12 then a.MONTH_12\n" +
    			"else 0 end\n" +
    			",0) as month_coef,\n" +
    			"calendar_importance_id,\n" +
    			"relative_coef\n" + 
    			"from QG_OWNER_ACT_CALENDAR_VW a,setup_month_year b " +
    			"where b.EFF_FISCAL_YEAR=?\n" +
    			"and a.activity_id=? and a.owner_id=? order by b.eff_fiscal_month");

    	dsCalendar.retrieve(fiscalYear, activityId, ownerId);
    	if (dsCalendar.rowCount() == 12)
    	{
    		ret = new double[12];
    		setRestrictZeros(dsCalendar.getItemInteger(1, "CALENDAR_IMPORTANCE_ID") != 1);
    		setRelativeCalendarImportance(dsCalendar.getItemInteger(1, "RELATIVE_COEF"));
    		double sum = 0.0;
    		for (int i = 1; i <= 12; i++)
    		{
    			sum += dsCalendar.getItemNumber(i, "MONTH_COEF");
    		}
    		
    		
    		// if calendar has all zeros then we will return an array of zeros, else we normalize
    		if (sum == 0.0)
    		{
    			for (int i = 1; i <= 12; i++)
    			{
        			ret[i-1] = 0.0;
        		}
    		}
    		else
    		{
    			for (int i = 1; i <= 12; i++)
    			{
        			ret[i-1] = dsCalendar.getItemNumber(i, "MONTH_COEF") / sum;
        		}
    		}
    	}
    	
    	return ret;
    }
    
    
    public void copyFromTxPmp() throws TRDIException // TX_PMP_FOR_PLAN_MATRIX case 7091
    {
        copyFrom("TX_PMP_FOR_PLAN_MATRIX","a.amount","a.inv_elem_id,a.CONTRACT_TOTAL_COST,(select b.inv_elem_name from ident_inv_element b " +
        			"where a.inv_elem_id=b.inv_elem_id) as inv_elem_name,"," and a.eff_fiscal_year="+fiscalYear);
    }
    
    
    public void copyFromInventoryBasedPlan() throws TRDIException // QG_INV_FOR_PLAN_MATRIX
    {
        copyFrom("QG_INV_FOR_PLAN_MATRIX","a.amount","","");
    }
    
    
    public void copyFromScnResults(Integer scenarioNum) throws TRDIException // copy scn
    {
        String additionalWc = "";
        if (scenarioNum != null)
        {
        	additionalWc += " AND A.MMS_SCENARIO_ID=" + scenarioNum;
        }
        copyFrom("SCENARIO_FOR_PLAN_MATRIX","a.amount","", additionalWc);
    }
    
    
    public void copyFromMWP(Integer MWPType) throws TRDIException // copy mwp
    {
        String additionalWc = "";
        if (MWPType != null)
        {
        	additionalWc += " AND A.PMS_MASTER_WP_TYPE_ID=" + MWPType;
        }
        copyFrom("MMWP_FOR_PLAN_MATRIX","a.amount","",additionalWc);
    }
    
    
    public void copyFromBacklog(Integer planNum, Integer fiscalYearNum) throws TRDIException
    {
        if (planNum == null || fiscalYearNum == null)
        {
        	return;
        }
        
        copyFrom("BACKLOG_FOR_PLAN_MATRIX", "a.amount", "", " and a.eff_fiscal_year=" + fiscalYearNum  + " and a.plan_id=" + planNum);
    }
    
    
    private void copyFrom(String tableName, String amountColumnName, String clientSpecificColumnName, String additionalWc) throws TRDIException
    {
    	// uses one of the following tables called from the 4 above methods:
    	//		QG_INV_FOR_PLAN_MATRIX
    	//		SCENARIO_FOR_PLAN_MATRIX
    	//		MMWP_FOR_PLAN_MATRIX
    	//		BACKLOG_FOR_PLAN_MATRIX
    	String sqlStr = "SELECT a.owner_id, a.asset_type_id, a.activity_id,\n" +
    			amountColumnName+" as amount,\n" +
    			"       (SELECT b.unit_cost\n" +
    			"          FROM qg_owner_ref_year b\n" +
    			"         WHERE b.eff_fiscal_year = "+fiscalYear+"\n" +
    			"           AND b.owner_id = a.owner_id\n" +
    			"           AND b.activity_id = a.activity_id) AS rate,\n" +
    			clientSpecificColumnName+
    			"       (SELECT c.man_hours\n" +
    			"          FROM qg_owner_ref_year c\n" +
    			"         WHERE c.eff_fiscal_year = "+fiscalYear+"\n" +
    			"           AND c.owner_id = a.owner_id\n" +
    			"           AND c.activity_id = a.activity_id) AS unit_hours,\n" +
    			"         (SELECT b.unit_cost\n" +
    			"            FROM qg_owner_ref_year b\n" +
    			"           WHERE b.eff_fiscal_year = "+fiscalYear+"\n" +
    			"             AND b.owner_id = a.owner_id\n" +
    			"             AND b.activity_id = a.activity_id)\n" +
    			"       * "+amountColumnName+"\n" +
    			"           AS total_cost,\n" +
    			"       (SELECT c.num_men\n" +
    			"          FROM qg_owner_ref_year c\n" +
    			"         WHERE c.owner_id = a.owner_id\n" +
    			"           AND c.activity_id = a.activity_id\n" +
    			"           AND c.eff_fiscal_year = "+fiscalYear+") AS num_men,\n" +
    			"       ROUND (  (  (SELECT c.man_hours\n" +
    			"                      FROM qg_owner_ref_year c\n" +
    			"                     WHERE c.eff_fiscal_year = "+fiscalYear+"\n" +
    			"                       AND c.owner_id = a.owner_id\n" +
    			"                       AND c.activity_id = a.activity_id)\n" +
    			"       * "+amountColumnName+"\n" +
    			"                 / "+hoursPerDay+"\n" +
    			"                )\n" +
    			"              / (SELECT c.num_men\n" +
    			"                   FROM qg_owner_ref_year c\n" +
    			"                  WHERE c.owner_id = a.owner_id\n" +
    			"                    AND c.activity_id = a.activity_id\n" +
    			"                    AND c.eff_fiscal_year = "+fiscalYear+"),\n" +
    			"              2\n" +
    			"             ) AS crew_days\n" +
    			"  FROM  " +tableName+" a where 1=1 ";
    	
    	String WhCl = UserSelection.getSelectionWc(dl,false,tableName,"a");
    	if (WhCl != null && WhCl.length() > 0)
    	{
    		sqlStr+=" AND "+WhCl ;
    	}
    	
    	sqlStr += additionalWc;
    	
    	if (accessRegPlan)
    	{
    		sqlStr += " AND a.owner_id=get_owner_id()";
    	}
    	
    	
    	DataStore dsData = dl.createDataStore(sqlStr);
    	dsData.retrieve();
    	dsPlan.deleteAllRows();
    	dsPlan.insertRows(dsData);
    	
    	for (int i = 1; i <= dsPlan.rowCount(); i++)
    	{
    		dsPlan.setItemNumber(i,"PLAN_ID",planId);
    		dsPlan.setItemNumber(i,"EFF_FISCAL_YEAR",fiscalYear);
    		if (dsPlan.getItemNumber(i,"AMOUNT") == null || dsPlan.getItemNumber(i,"UNIT_HOURS") == null)
    		{
    			for (int j = 1; j <= 12; j++) {
    				dsPlan.setItemNumber(i,"FTE_"+j,0);
    			}
    		}
    		else
    		{
    			distributeMenHours(i);
    		}
    	}
    }


    /**
     * This method will level labor resources by formulating a linear programming problem, passing the values to
     * solver, and then retrieving solutions.
     * 
     * @param monthDays	An array representing the amount of days (or hours -- units don't really matter) available
     * 					for each month of the year.
     * @param activityIsMaster If true this specifies that the plan is dominant, and we should calculate the FTE
     * 					required to satisfy that value.  Otherwise, the FTE is dominant and we are returning the
     * 					amount of activity that is possible for the FTE given by the user (or system).
     * @throws TRDIException
     */
    public void levelFTE(double[] monthDays, boolean activityIsMaster) throws TRDIException
    {
    	DataStore dsAggPlan = getAggregatedPlan();
    	double[] activityDays = new double[dsAggPlan.rowCount()];
    	for (int i = 1; i <= dsAggPlan.rowCount(); i++)
    	{
    		activityDays[i-1] = dsAggPlan.getItemNumber(i, "TOTAL_FTE");
    	}
    	
    	// verify that the arrays have values
    	double totalActivity = 0.0;
    	double totalMonthDays = 0.0;
    	for (int i = 0; i < activityDays.length; i++) {
    		totalActivity += activityDays[i];
    	}

    	for (int i = 0; i < monthDays.length; i++) {
    		totalMonthDays += monthDays[i];
    	}

    	if (totalActivity == 0.0 || totalMonthDays == 0.0) {
    		throw new TRDIException(972);
    	}
    	
    	
    	// scale the two arrays appropriately
    	if (activityIsMaster)
    	{
    		for (int i = 0; i < monthDays.length; i++)
    		{
    			monthDays[i] = (monthDays[i] / totalMonthDays) * totalActivity;
    		}
    	}
    	else
    	{
    		// first check the rows and throw an error if scaling is not possible
    		double totalProtected = 0.0;
    		double totalUnprotected = 0.0;
    		double coeff = 1.0;
    		ArrayList<Integer> unprotectedRows = new ArrayList<Integer>();
    		for (int i = 1; i <= dsAggPlan.rowCount(); i++)
    		{
    			if (dsAggPlan.getItemInteger(i, "ELEM_EXISTS") == 0)
    			{
    				totalUnprotected += dsAggPlan.getItemNumber(i, "TOTAL_FTE");
    				unprotectedRows.add(new Integer(i));
    			}
    			else
    			{
    				totalProtected += dsAggPlan.getItemNumber(i, "TOTAL_FTE");
    			}
    		}
    		
    		if (totalProtected > totalMonthDays)
    		{
    			// Leveling not possible because there are not enough resources to perform mandatory activities...
    			throw new TRDIException(1613, totalProtected - totalMonthDays);
    		}
    		else if (totalProtected < totalMonthDays && unprotectedRows.size() == 0)
    		{
    			// Leveling not possible because there are not enough activities to occupy resources...
    			throw new TRDIException(1614, totalMonthDays - totalProtected);
    		}
    		else if (totalProtected == totalMonthDays)
    		{
    			coeff = 0.0;
    		}
    		
    		
    		// scale only the unprotected rows
    		if (totalUnprotected != 0)
    		{
    			coeff = (totalMonthDays - totalProtected) / totalUnprotected;
    		}
    		
    		if (coeff != 1.0)
    		{
    			for (int i = 0; i < unprotectedRows.size(); i++)
	    		{
    				int rowNum = unprotectedRows.get(i);
	    			activityDays[rowNum-1] *= coeff;
	    			// also set scaled value in the TOTAL_FTE column for the corresponding row in dsPlan
	    			dsPlan.setItemNumber(dsAggPlan.getItemInteger(rowNum, "UNAGG_PLAN_ROW"), "TOTAL_FTE", activityDays[rowNum-1]);
	    		}
    		}
    	}
    	
    	
    	// determine the precision that we need to use via the TOTAL_MAN_DAYS column
    	int precision = getScale();


    	/* Here we have to check for a difference in precision.  Oracle will only store 4 digits after the decimal
    	 * and therefore, rounding errors can occur which cause us to send data to solver which is infeasible.  For
    	 * this reason we will push the values to the nearest integer.  Since K constraints (activity) have >= sign
    	 * we will call floor.  For M constraints we must be <= so we will use ceil.  At the same time we will also
    	 * scale the values to the appropriate level of precision.
    	 */
    	for (int i = 0; i < activityDays.length; i++) {
    		activityDays[i] = Math.floor(activityDays[i] * Math.pow(10, precision));
    	}
    	
    	for (int i = 0; i < monthDays.length; i++) {
    		monthDays[i] = Math.ceil(monthDays[i] * Math.pow(10, precision));
    	}

    	

    	/* USER_ANALYSIS_SOLVER_REQ (input table for rows):
    	 * Defines the rows (objective and constraints), their signs (in the [in]equality), and right-hand-side (RHS) for all rows,
    	 * including the objective.  This information is sufficient to create the first and third portion of a .mps file.  Note
    	 * that objective will have no sign and no RHS as this is a value we are attempting to locate.
    	 */
    	DataStore dsReq = prepareSolverDS("USER_ANALYSIS_SOLVER_REQ");

    	/* USER_ANALYSIS_SOLVER_MAIN (input table for major constraints and objective function):
    	 * Defines the matrix of coefficients for variables and constraints.  This defines the matrix to connect rows with columns.
    	 */
    	DataStore dsMain = prepareSolverDS("USER_ANALYSIS_SOLVER_MAIN");

    	/* USER_ANALYSIS_SOLVER_LVL (output table which stores solutions from solver):
    	 * For each activity this contains the solution for each month of a plan row.
    	 */
    	DataStore dsSolver = prepareSolverDS("USER_ANALYSIS_SOLVER_LVL");
    	
    	/* USER_ANALYSIS_SOLVER_COL (input table for columns):
    	 * Defines the matrix of coefficients for variables and constraints.  This defines the matrix to connect rows with columns.
    	 */
    	DataStore dsCol = prepareSolverDS("USER_ANALYSIS_SOLVER_COL");
    	
    	/* USER_ANALYSIS_SOLVER_BND:
    	 * Stores bound information for each X column.
    	 */
    	DataStore dsBnd = prepareSolverDS("USER_ANALYSIS_SOLVER_BND");


    	String userId = dl.getSession().getUserId();

    	// insert obj function row
    	int row = dsMain.insertRow(0);
    	dsMain.setItemString(row, "USER_ID", userId);
    	dsMain.setItemString(row, "CONST_SIGN", "N");
    	dsMain.setItemString(row, "CONSTR_NAME", "AAA");
    	dsMain.setItemNumber(row, "IS_MAX", 0);

    	// insert the K-type constraints (planned activity for the year, by activity)
    	for (int i = 0; i < activityDays.length; i++)
    	{
    		if (activityDays[i] == 0.0) {
    			continue;
    		}

    		row = dsMain.insertRow(0);
    		dsMain.setItemString(row, "USER_ID", userId);
    		dsMain.setItemString(row, "CONST_SIGN", "G");
    		dsMain.setItemString(row, "CONSTR_NAME", "K"+(i+1));
    		dsMain.setItemNumber(row, "RHS", activityDays[i]);

    		// insert each month for this row into USER_ANALYSIS_SOLVER_LVL so we can retrieve solutions
    		for (int j = 1; j <= 12; j++)
    		{
    			dsSolver.insertRows(dsAggPlan, i+1, i+1);
    			dsSolver.setItemNumber(dsSolver.rowCount(), "EFF_FISCAL_MONTH", j);
    			dsSolver.setItemNumber(dsSolver.rowCount(), "PLAN_ROW", i+1);
    		}

    	}

    	// insert the M-type constraints (FTE values for each month)
    	for (int i = 1; i <= 12; i++)
    	{
    		row = dsMain.insertRow(0);
    		dsMain.setItemString(row, "USER_ID", userId);
    		dsMain.setItemString(row, "CONST_SIGN", "L");
    		dsMain.setItemString(row, "CONSTR_NAME", "M"+i);
    		dsMain.setItemNumber(row, "RHS", monthDays[i-1]);
    	}

    	for (int i = 1; i <= dsSolver.rowCount(); i++)
    	{
    		dsSolver.setItemString(i, "USER_ID", userId);
    		dsSolver.setItemNumber(i, "REC_NUM", i);
    	}

    	
    	String varNum, paddedNum, X, Y, YH, YL;
    	double[] coeff = null;
    	int padLength = 5;
    	
    	for (int i = 0; i < activityDays.length; i++)
    	{
    		coeff = getCalendarCoefficients(dsAggPlan.getItemInteger(i+1, "OWNER_ID"), dsAggPlan.getItemInteger(i+1, "ACTIVITY_ID"));
    		this.setRestrictZeros(this.isRestrictZeros() || !dsAggPlan.isItemNull(i+1, "PLAN_LINE_PRIORITY"));
    		
    		for (int j = 0; j < coeff.length; j++)
    		{
    			varNum = "" + (12*i + (j+1));
    			paddedNum = TRDIUtils.leftPad(varNum, padLength, '0');
    			X = "X" + paddedNum;
    			Y = "Y" + paddedNum;
    			YH = "YH" + varNum;
    			YL = "YL" + varNum;
    			
    			// insert K and M columns
    			insertDsCol(dsCol, X, "K" + (i+1), 1);
    			insertDsCol(dsCol, X, "M" + (j+1), 1);
    			
    			/* Here we insert YH and YL constraints so that this problem becomes linear.  We are formulating the problem using
    	    	 * absolute value, which is not linear.  We want the sum of | Xij/Aj - Pij | > min, so we split this into 2 parts:
    	    	 *
    	    	 * 		YH = Yij + Xij/Aj >=  Pij
    	    	 * 		YL = Yij - Xij/Aj >= -Pij
    	    	 * 
    	    	 * 
    	    	 * Note that X is the coefficient to solver, A is the totalActivity for this row, and P is the normalized
    	    	 * percentage of work that is to be done for the current month in the activity calendar.
    	    	 */
    			insertDsCol(dsCol, X, YH, -1/activityDays[i]);
    			insertDsCol(dsCol, X, YL, 1/activityDays[i]);
    			
        		insertDsCol(dsCol, Y, "AAA", getRelativeCalendarImportance());
        		insertDsCol(dsCol, Y, YH, 1);
        		insertDsCol(dsCol, Y, YL, 1);
        		
        		// insert RHS for YH/YL constraints
        		insertDsReq(dsReq, "G", YH, -1*coeff[j]);
        		insertDsReq(dsReq, "G", YL, coeff[j]);
        		
        		// insert bound for X
        		insertBound(dsBnd, coeff[j], X);
    		}
    	}

    	
    	dsReq.insertRows(dsMain);
    	dl.saveData(dsSolver, dsMain, dsReq, dsCol, dsBnd);
    	
    	// update the objective row in USER_ANALYSIS_SOLVER_MAIN so that maxNode is set high enough
    	dl.executeSQL("UPDATE USER_ANALYSIS_SOLVER_MAIN \n" +
    			"SET MAX_NODE=(SELECT 2 * COUNT(1) FROM USER_ANALYSIS_SOLVER_LVL WHERE USER_ID=get_user_id()) \n" +
    			"WHERE USER_ID=get_user_id() AND CONST_SIGN='N'");

    	// with all constraints in the table we want to create our MPS file and call solver
    	CbcSolverWrapper.execute(dl, false, "USER_ANALYSIS_SOLVER_LVL");
    	
    	// zero out all of the current values in the plan
    	for (int i = 1; i <= dsPlan.rowCount(); i++)
    	{
    		for (int j = 1; j <= 12; j++)
    			dsPlan.setItemNumber(i, "FTE_"+j, 0);
    	}

    	// get solutions back from solver and put them into the appropriate tables to update values in the window
    	dsSolver.retrieve();
    	int currRow;
    	String colName;
    	Double solution;
    	DataStore dsCurrPlan = dl.isColumnInTable("INV_ELEM_ID", "MASTER_PLAN_MATRIX") ? dsAggPlan : dsPlan;
    	for (int i = 1; i <= dsSolver.rowCount(); i++)
    	{
    		currRow = dsSolver.getItemInteger(i, "PLAN_ROW");
    		colName = "FTE_" + dsSolver.getItemInteger(i, "EFF_FISCAL_MONTH");
    		solution = dsSolver.getItemNumber(i, "SOLUTION") / Math.pow(10, precision);
    		dsCurrPlan.setItemNumber(currRow, colName, solution);
    	}
    	
    	// unpack the aggregated solutions back into the total plan
    	if (dl.isColumnInTable("INV_ELEM_ID", "MASTER_PLAN_MATRIX"))
    	{
    		unpackAggregatedSolutions(dsAggPlan);
    	}
    }
    
    
    /**
     * Aggregates the rows in the {@link DataStore} dsPlan which have the same <OWNER_ID, ACTIVITY_ID> pair,
     * and also have a not-null INV_ELEM_ID.
     * @return The aggregated {@link DataStore} object. 
     * @throws TRDIException
     */
    private DataStore getAggregatedPlan() throws TRDIException
    {
    	DataStore dsAggPlan = new DataStore();
    	
    	// create appropriate columns in aggregated ds (mostly the same as dsPlan)
    	List<DataColumn> columns = dsPlan.getColumns();
    	for (DataColumn col : columns)
    	{
    		if (!col.getName().equals("INV_ELEM_ID"))
    		{
    			dsAggPlan.appendColumn(col);
    		}
    	}
    	
    	/* Append ELEM_EXISTS and UNAGG_PLAN_ROW columns.  The UNAGG_PLAN_ROW is really only valid
    	 * for rows that have no INV_ELEM_ID, and will be used to "fix" the TOTAL_FTE value for
    	 * unprotected rows when they get scaled in a level by given/existing FTE.
    	 */
    	dsAggPlan.appendColumn(new DataColumn("ELEM_EXISTS", TRDIConstants.DATA_TYPE_INTEGER));
    	dsAggPlan.appendColumn(new DataColumn("UNAGG_PLAN_ROW", TRDIConstants.DATA_TYPE_INTEGER));
    	
    	// NCDOT does not use INV_ELEM_ID so we can't perform any aggregation in a case like that
    	if (!dl.isColumnInTable("INV_ELEM_ID", "MASTER_PLAN_MATRIX"))
    	{
    		for (int i = 1; i <= dsPlan.rowCount(); i++)
    		{
    			dsAggPlan.insertRows(dsPlan, i, i);
    			dsAggPlan.setItemNumber(dsAggPlan.rowCount(), "ELEM_EXISTS", 0);
    			dsAggPlan.setItemNumber(dsAggPlan.rowCount(), "UNAGG_PLAN_ROW", i);
    		}
    	}
    	else
    	{
	    	Object[] currValues = new Object[3];
	    	dsAggPlan.buildIndex("aggIndex", "OWNER_ID", "ACTIVITY_ID", "ELEM_EXISTS");
	    	
	    	// keep track of max priority and the rows with INV_ELEM_ID and a null priority (so we can fill them)
	    	int maxPriority = 0;
	    	ArrayList<Integer> nullPriorityRows = new ArrayList<Integer>();
	    	
	    	
	    	// create a new row if necessary, or simply add to the current values in an aggregate row
	    	for (int i = 1; i <= dsPlan.rowCount(); i++)
	    	{
	    		currValues[0] = dsPlan.getItemInteger(i, "OWNER_ID");
	    		currValues[1] = dsPlan.getItemInteger(i, "ACTIVITY_ID");
	    		currValues[2] = dsPlan.isItemNull(i, "INV_ELEM_ID") ? new Integer(0) : new Integer(1);
	    		
	    		int row = dsAggPlan.findByIndex("aggIndex", currValues);
	    		
	    		if (row > 0)	// add to an existing row
	    		{
	    			double currVal = dsAggPlan.isItemNull(row, "TOTAL_FTE") ? 0 : dsAggPlan.getItemNumber(row, "TOTAL_FTE");
	    			double planVal = dsPlan.isItemNull(i, "TOTAL_FTE") ? 0 : dsPlan.getItemNumber(i, "TOTAL_FTE");
	    			dsAggPlan.setItemNumber(row, "TOTAL_FTE", currVal+planVal);
	    		}
	    		else	// insert a new row
	    		{
	    			dsAggPlan.insertRows(dsPlan, i, i);
	    			dsAggPlan.setItemNumber(dsAggPlan.rowCount(), "ELEM_EXISTS", (Integer)currValues[2]);
	    			dsAggPlan.setItemNumber(dsAggPlan.rowCount(), "UNAGG_PLAN_ROW", i);
	    		}
	    		
	    		// keep track of max priority
	    		if (!dsPlan.isItemNull(i, "PLAN_LINE_PRIORITY"))
	    		{
	    			maxPriority = (int)Math.max(maxPriority, dsPlan.getItemInteger(i, "PLAN_LINE_PRIORITY"));
	    		}
	    		else if ((Integer)currValues[2] == 1 && dsPlan.isItemNull(i, "PLAN_LINE_PRIORITY"))
	    		{
	    			nullPriorityRows.add(new Integer(i));
	    		}
	    	}
	    	
	    	// if there are any rows with INV_ELEM_ID defined but no priority, then assign max+1 for when we unpack
	    	maxPriority++;
	    	for (int i = 0; i < nullPriorityRows.size(); i++)
	    	{
	    		dsPlan.setItemNumber(nullPriorityRows.get(i), "PLAN_LINE_PRIORITY", maxPriority);
	    	}
	    }
    	
    	
    	return dsAggPlan;
    }
    
    
    /**
     * Unpacks the aggregated solutions back into the plan, where applicable.
     * @param dsAggPlan The {@link DataStore} representing aggregated values for rows in dsPlan that have
     * the same <OWNER_ID, ACTIVITY_ID> pair and a not-null INV_ELEM_ID as well as all standalone rows.
     * @throws TRDIException
     */
    private void unpackAggregatedSolutions(DataStore dsAggPlan) throws TRDIException
    {
    	dsPlan.sort(dl, "OWNER_ID ASC, ACTIVITY_ID ASC, PLAN_LINE_PRIORITY ASC");
    	dsPlan.buildIndex("ownerActIndex", "OWNER_ID", "ACTIVITY_ID");
    	Object[] indexValues = new Object[2];
    	int[] rows;
    	int currRow;
    	double[] fteValues = new double[12];
    	boolean elemExists;
    	double totalFte;
    	
    	for (int i = 1; i <= dsAggPlan.rowCount(); i++)
    	{
    		for (int month = 0; month < 12; month++)
    		{
    			fteValues[month] = dsAggPlan.getItemNumber(i, "FTE_" + (month+1));
    		}
    		
    		indexValues[0] = dsAggPlan.getItemInteger(i, "OWNER_ID");
    		indexValues[1] = dsAggPlan.getItemInteger(i, "ACTIVITY_ID");
    		elemExists = dsAggPlan.getItemInteger(i, "ELEM_EXISTS") == 1;
    		rows = dsPlan.findAllByIndex("ownerActIndex", indexValues);
    		
    		for (int j = 0; j < rows.length; j++)
    		{
    			currRow = rows[j];
    			
    			
    			if (elemExists && dsPlan.isItemNull(currRow, "INV_ELEM_ID"))
    			{
    				// We are currently working with all aggregated rows (so ignore the row where INV_ELEM_ID is null)
    				continue;
    			}
    			else if (!elemExists && !dsPlan.isItemNull(currRow, "INV_ELEM_ID"))
    			{
    				// We are currently working with just one row -- it was not aggregated at all (so ignore everything else)
    				continue;
    			}
    			
    			
    			// distribute non-zero months until TOTAL_FTE is exhausted
    			totalFte = dsPlan.getItemNumber(currRow, "TOTAL_FTE");
    			for (int month = 0; month < 12; month++)
    			{
    				if (fteValues[month] <= totalFte)
    				{
    					dsPlan.setItemNumber(currRow, "FTE_" + (month+1), fteValues[month]);
    					totalFte -= fteValues[month];
    					fteValues[month] = 0;
    				}
    				else
    				{
    					dsPlan.setItemNumber(currRow, "FTE_" + (month+1), totalFte);
    					fteValues[month] -= totalFte;
    					break;
    				}
    			}
    		}
    	}
    }
    
    
    /**
     * Inserts a row into the USER_ANALYSIS_SOLVER_COL table.
     * @param dsCol A {@link DataStore} representing the USER_ANALYSIS_SOLVER_COL table.
     * @param columnName The value to insert into the COLUMN_NAME column.
     * @param constrName The value to insert into the CONSTR_NAME column.
     * @param columnCoef The value to insert into the COLUMN_COEF column.
     * @throws TRDIException
     */
    private void insertDsCol(DataStore dsCol, String columnName, String constrName, double columnCoef) throws TRDIException
    {
    	int row = dsCol.insertRow(0);
    	dsCol.setItemString(row, "USER_ID", dl.getSession().getUserId());
    	dsCol.setItemString(row, "COLUMN_NAME", columnName);
		dsCol.setItemString(row, "CONSTR_NAME", constrName);
		dsCol.setItemNumber(row, "COLUMN_COEF", columnCoef);
    }
    
    
    /**
     * Inserts a row into the USER_ANALYSIS_SOLVER_REQ table.
     * @param dsReq A {@link DataStore} representing the USER_ANALYSIS_SOLVER_REQ table.
     * @param constSign The value to insert into the CONST_SIGN column.
     * @param constrName The value to insert into the CONSTR_NAME column.
     * @param rhs The value to insert into the RHS column.
     * @throws TRDIException
     */
    private void insertDsReq(DataStore dsReq, String constSign, String constrName, double rhs) throws TRDIException
    {
    	int row = dsReq.insertRow(0);
    	dsReq.setItemString(row, "USER_ID", dl.getSession().getUserId());
    	dsReq.setItemString(row, "CONST_SIGN", constSign);
    	dsReq.setItemString(row, "CONSTR_NAME", constrName);
    	dsReq.setItemNumber(row, "RHS", rhs);
    }
    
    
    /**
     * Inserts a row into the USER_ANALYSIS_SOLVER_BND table.
     * @param dsBnd A {@link DataStore} representing the USER_ANALYSIS_SOLVER_BND table.
     * @param columnName The value to insert into the COLUMN_NAME column.
     * @param boundType The value to insert into the BOUND_TYPE column.
     * @param boundRhs The value to insert into the BOUND_RHS column.
     * @throws TRDIException
     */
    private void insertDsBnd(DataStore dsBnd, String columnName, String boundType, Integer boundRhs) throws TRDIException
    {
    	int row = dsBnd.insertRow(0);
    	dsBnd.setItemString(row, "USER_ID", dl.getSession().getUserId());
    	dsBnd.setItemString(row, "COLUMN_NAME", columnName);
    	dsBnd.setItemString(row, "BOUND_TYPE", boundType);
    	dsBnd.setItemNumber(row, "BOUND_RHS", boundRhs);
    }
    
    
    /**
     * Inserts a bound for the given <tt>constraint</tt> so the .mps file is created correctly.  If the calendar is
     * restricting zeros (making them mandatory) and the <tt>coeff</tt> is 0, then we will insert a bound to fix
     * <tt>constraint</tt> to 0.  Otherwise, we just let it take any integer value >= 0.
     * @param dsBnd A {@link DataStore} representing the USER_ANALYSIS_SOLVER_BOUND table.
     * @param coeff The activity coefficient for the current month.
     * @param constraint The name of the current constraint.
     */
    private void insertBound(DataStore dsBnd, double coeff, String constraint) throws TRDIException
    {
    	String boundType = "";
    	Integer boundRhs = null;
    	
    	if (isRestrictZeros() && coeff == 0.0)
		{
			boundType = "FX";
			boundRhs = new Integer(0);
		}
		else
		{
			boundType = "PL";
			boundRhs = null;
		}
		
		insertDsBnd(dsBnd, constraint, boundType, boundRhs);
    }
    
    
    /**
     * Cleans out (deletes rows from) the specified table for the current USER_ID, and returns a DataStore object with zero rows.
     * 
     * @param tableName The table to be cleaned out.
     * @return A {@link DataStore} object representing a "SELECT *" query run against <code>tableName</code>.
     * @throws TRDIException
     */
    private DataStore prepareSolverDS(String tableName) throws TRDIException
    {
    	dl.executeSQL("delete from " + tableName + " where user_id=get_user_id()");
    	DataStore ds = dl.createDataStore("select * from " + tableName + " where user_id=get_user_id()");
    	ds.retrieve();
    	ds.setUpdateTable(tableName);
    	return ds;
    }
    
    
    /**
     * Aggregates data to populate the DataWindow dw_totals (showing Planned, Available, and Difference).
     * 
     * @return A {@link DataStore} representing the values for the DataWindow dw_totals.
     * @throws TRDIException
     */
    public DataStore makeTotals() throws TRDIException
    {
    	String windowType = dl.getSession().getCurrentMainWindow().getWindowParams();
    	double totalDays;
    	DataStore dsNumMen = null;
    	//todo temp for IN;

    	if (dl.isColumnInTable("NUM_MEN","QG_OWNER_REF"))
    		dsNumMen=dl.createDataStore("select NUM_MEN from qg_owner_ref_year where owner_id=? and activity_id=? and eff_fiscal_year=?");
    	
    	for (int i = 1; i <= dsPlan.rowCount(); i++)
    	{
    		totalDays = (dsPlan.getItemNumber(i,"FTE_1")!=null ? dsPlan.getItemNumber(i,"FTE_1") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_2")!=null ? dsPlan.getItemNumber(i,"FTE_2") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_3")!=null ? dsPlan.getItemNumber(i,"FTE_3") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_4")!=null ? dsPlan.getItemNumber(i,"FTE_4") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_5")!=null ? dsPlan.getItemNumber(i,"FTE_5") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_6")!=null ? dsPlan.getItemNumber(i,"FTE_6") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_7")!=null ? dsPlan.getItemNumber(i,"FTE_7") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_8")!=null ? dsPlan.getItemNumber(i,"FTE_8") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_9")!=null ? dsPlan.getItemNumber(i,"FTE_9") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_10")!=null ? dsPlan.getItemNumber(i,"FTE_10") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_11")!=null ? dsPlan.getItemNumber(i,"FTE_11") : 0) +
    				(dsPlan.getItemNumber(i,"FTE_12")!=null ? dsPlan.getItemNumber(i,"FTE_12") : 0);
    		
    		if (dsPlan.isValidColumn("TOTAL_FTE"))
    			dsPlan.setItemNumber(i,"TOTAL_FTE",totalDays);

    		if (dsPlan.isValidColumn("CALC_FTE"))
    		{
    			// get the appropriate scale
    			int precision = getScale();

    			// calculate value, if possible, and round it to appropriate scale
    			Double unitHours = dsPlan.getItemNumber(i, "UNIT_HOURS");
    			Double totalCost = dsPlan.getItemNumber(i, "TOTAL_COST");
    			Double rate = dsPlan.getItemNumber(i, "RATE");
    			Double number = null;
    			if (unitHours != null && totalCost != null && rate != null && rate > 0)
    			{
    				number = unitHours * totalCost / rate / hoursPerDay;
    				number = TRDIUtils.roundNumber(number, precision);
    			}

    			dsPlan.setItemNumber(i, "CALC_FTE", number);
    		}
    		
    		if (dsPlan.isValidColumn("AMOUNT"))
    		{
    			dsPlan.setItemNumber(i,"AMOUNT",
    				(dsPlan.getItemNumber(i,"RATE")!=null
    				&& dsPlan.getItemNumber(i,"RATE")>0
    				&& dsPlan.getItemNumber(i,"TOTAL_COST")!=null?
    						(dsPlan.getItemNumber(i,"TOTAL_COST")/dsPlan.getItemNumber(i,"RATE"))
    						:null));
    		}
    		
    		if (dsPlan.isValidColumn("DELTA_FTE"))
    			dsPlan.setItemNumber(i,"DELTA_FTE",dsPlan.getItemNumber(i,"TOTAL_FTE") -(dsPlan.getItemNumber(i,"CALC_FTE")!=null ? dsPlan.getItemNumber(i,"CALC_FTE") : 0));
    		
    		if (dsPlan.isValidColumn("CALC_COST")) dsPlan.setItemNumber(i,"CALC_COST",(dsPlan.getItemNumber(i,"UNIT_HOURS")!=null &&
    				dsPlan.getItemNumber(i,"UNIT_HOURS")>0 && dsPlan.getItemNumber(i,"RATE")!=null  ?
    						dsPlan.getItemNumber(i,"RATE")*hoursPerDay*totalDays/ dsPlan.getItemNumber(i,"UNIT_HOURS") :
    							0));
    		
    		//  IN additional columns
    		if (dsPlan.isValidColumn("CREW_DAYS"))
    		{
    			if (dsPlan.getItemNumber(i,"NUM_MEN")==null)
    			{
    				dsNumMen.retrieve(dsPlan.getItemInteger(i,"OWNER_ID"),dsPlan.getItemInteger(i,"ACTIVITY_ID"),fiscalYear);
    				if (dsNumMen.rowCount()>0 && dsNumMen.getItemNumber(1,1)!=null)
    					dsPlan.setItemNumber(i,"NUM_MEN",dsNumMen.getItemNumber(1,1));

    			}
    			if (dsPlan.getItemNumber(i,"NUM_MEN")!=null && dsPlan.getItemNumber(i,"NUM_MEN")>0)
    				dsPlan.setItemNumber(i,"CREW_DAYS",dsPlan.getItemNumber(i,"TOTAL_FTE")/dsPlan.getItemNumber(i,"NUM_MEN"));
    		}
    		
    		if (dsPlan.isValidColumn("PLAN_AMOUNT") && dsPlan.getItemNumber(i,"UNIT_HOURS")!=null && dsPlan.getItemNumber(i,"UNIT_HOURS")>0)
    		{
    			dsPlan.setItemNumber(i,"PLAN_AMOUNT",totalDays * hoursPerDay / dsPlan.getItemNumber(i,"UNIT_HOURS"));
    		}
    		
    		//  TX additional columns
    		if (dsPlan.isValidColumn("TOTAL_PLAN_COST") && dsPlan.isValidColumn("contract_total_cost") )
    		{
    			dsPlan.setItemNumber(i,"TOTAL_PLAN_COST",  (dsPlan.getItemNumber(i,"CONTRACT_TOTAL_COST")==null?0:dsPlan.getItemNumber(i,"CONTRACT_TOTAL_COST")) + (dsPlan.getItemNumber(i,"TOTAL_COST")==null?0:dsPlan.getItemNumber(i,"TOTAL_COST")));
    		}
    	}

    	dsTotals.deleteAllRows();
    	Double fte_1=0.0,fte_2=0.0,fte_3=0.0,fte_4=0.0,fte_5=0.0,fte_6=0.0,fte_7=0.0,fte_8=0.0,fte_9=0.0,fte_10=0.0,fte_11=0.0,fte_12=0.0,delta_fte=0.0,total_cost=0.0,fte_total;
    	for(int i=1; i<=dsPlan.rowCount(); i++){
    		fte_1 = fte_1 + (dsPlan.getItemNumber(i,"FTE_1")!=null ? dsPlan.getItemNumber(i,"FTE_1") : 0);
    		fte_2 = fte_2 + (dsPlan.getItemNumber(i,"FTE_2")!=null ? dsPlan.getItemNumber(i,"FTE_2") : 0);
    		fte_3 = fte_3 + (dsPlan.getItemNumber(i,"FTE_3")!=null ? dsPlan.getItemNumber(i,"FTE_3") : 0);
    		fte_4 = fte_4 + (dsPlan.getItemNumber(i,"FTE_4")!=null ? dsPlan.getItemNumber(i,"FTE_4") : 0);
    		fte_5 = fte_5 + (dsPlan.getItemNumber(i,"FTE_5")!=null ? dsPlan.getItemNumber(i,"FTE_5") : 0);
    		fte_6 = fte_6 + (dsPlan.getItemNumber(i,"FTE_6")!=null ? dsPlan.getItemNumber(i,"FTE_6") : 0);
    		fte_7 = fte_7 + (dsPlan.getItemNumber(i,"FTE_7")!=null ? dsPlan.getItemNumber(i,"FTE_7") : 0);
    		fte_8 = fte_8 + (dsPlan.getItemNumber(i,"FTE_8")!=null ? dsPlan.getItemNumber(i,"FTE_8") : 0);
    		fte_9 = fte_9 + (dsPlan.getItemNumber(i,"FTE_9")!=null ? dsPlan.getItemNumber(i,"FTE_9") : 0);
    		fte_10 = fte_10 + (dsPlan.getItemNumber(i,"FTE_10")!=null ? dsPlan.getItemNumber(i,"FTE_10") : 0);
    		fte_11 = fte_11 + (dsPlan.getItemNumber(i,"FTE_11")!=null ? dsPlan.getItemNumber(i,"FTE_11") : 0);
    		fte_12 = fte_12 + (dsPlan.getItemNumber(i,"FTE_12")!=null ? dsPlan.getItemNumber(i,"FTE_12") : 0);
    		if (dsPlan.isValidColumn("DELTA_FTE")) delta_fte = delta_fte + (dsPlan.getItemNumber(i,"DELTA_FTE")!=null ? dsPlan.getItemNumber(i,"DELTA_FTE") : 0);
    		total_cost = total_cost + (dsPlan.getItemNumber(i,"TOTAL_COST")!=null ? dsPlan.getItemNumber(i,"TOTAL_COST") : 0);
    	}
    	fte_total = fte_1+fte_2+fte_3+fte_4+fte_5+fte_6+fte_7+fte_8+fte_9+fte_10+fte_11+fte_12;


    	//Summations of the info in Plan - 1-st row  will be processed depending on client business rules:
    	// ut (0)- fte(divide to monthDays), IN (1)- mandays (no processing); WY (2) - manhours   ( (no processing);
    	DataStore ds_planned=dl.createDataStore("select 'Planned  ' as descr," +
    			fte_1+" as FTE_1,"+fte_2+" as FTE_2,"+fte_3+" as FTE_3," +
    			fte_4+" as FTE_4,"+fte_5+" as FTE_5,"+fte_6+" as FTE_6," +
    			fte_7+" as FTE_7,"+fte_8+" as FTE_8,"+fte_9+" as FTE_9," +
    			fte_10+" as FTE_10,"+fte_11+" as FTE_11,"+fte_12+" as FTE_12," +
    			delta_fte+" as DELTA_FTE,\n" +
    			fte_total+" as TOTAL_MAN_DAYS," +
    			total_cost+" as TOTAL_COST\n" +
    			"from dual");
    	ds_planned.retrieve();

    	dsTotals.copyRows(ds_planned);
    	// available men  - 2-nd row will be processed depending on client business rules:
    	// ut (0)- fte (no Processing), IN (1)- mandays (* to monthDays); WY (2) - manhours  ( * to monthHours)
    	String sql_str = "select 'Available  ' as descr,\n" +
    			"sum(nvl(a.FTE_1,0)) as FTE_1,sum(nvl(a.FTE_2,0)) as FTE_2,sum(nvl(a.FTE_3,0)) as FTE_3,\n" +
    			"sum(nvl(a.FTE_4,0)) as FTE_4,sum(nvl(a.FTE_5,0)) as FTE_5,sum(nvl(a.FTE_6,0)) as FTE_6,\n" +
    			"sum(nvl(a.FTE_7,0)) as FTE_7,sum(nvl(a.FTE_8,0)) as FTE_8,sum(nvl(a.FTE_9,0)) as FTE_9,\n" +
    			"sum(nvl(a.FTE_10,0)) as FTE_10,sum(nvl(a.FTE_11,0)) as FTE_11,sum(nvl(a.FTE_12,0)) as FTE_12,\n" +
    			"0 as DELTA_FTE,\n" +
    			"sum(nvl(a.FTE_1,0))+sum(nvl(a.FTE_2,0))+sum(nvl(a.FTE_3,0))+" +
    			"sum(nvl(a.FTE_4,0))+sum(nvl(a.FTE_5,0))+sum(nvl(a.FTE_6,0))+" +
    			"sum(nvl(a.FTE_7,0))+sum(nvl(a.FTE_8,0))+sum(nvl(a.FTE_9,0))+" +
    			"sum(nvl(a.FTE_10,0))+sum(nvl(a.FTE_11,0))+sum(nvl(a.FTE_12,0)) as TOTAL_MAN_DAYS\n" +
    			"from OWNER_MONTH_FTE a where a.module_id=get_module_id() and a.EFF_FISCAL_YEAR = ?";

    	// make sure to ONLY filter by OWNER_ID
    	List<String> filterCols = new ArrayList<String>();
    	filterCols.add("OWNER_ID");
    	String wc= UserSelection.getSelectionWc(dl,accessRegPlan,filterCols);
    	if (wc!=null && wc.length()>0) sql_str+=" AND "+wc;
    	DataStore ds_available=dl.createDataStore(sql_str);
    	ds_available.retrieve(fiscalYear);

    	if(ds_available.rowCount()>0)
    		dsTotals.copyRows(ds_available);

    	sql_str="select a.EFF_FISCAL_MONTH,\n" +
    			"(select sum(eff_hours)  from setup_custom_calendar_det\n" +
    			"where CALENDAR_ID = 1 and TO_NUMBER (TO_CHAR (eff_date, 'YYYY')) =eff_year\n" +
    			"and TO_NUMBER (TO_CHAR (eff_date, 'MM')) =eff_month) as month_hours\n" +
    			"from SETUP_MONTH_YEAR a where EFF_FISCAL_YEAR = ? order by eff_fiscal_month";
    	DataStore ds_month_hours = dl.createDataStore(sql_str);
    	ds_month_hours.retrieve(fiscalYear);
    	for (int i=1;i<=12;i++){
    		if(ds_month_hours.isItemNull(i,"MONTH_HOURS")) {
    			throw new TRDIException(1285, new DataColumn("EFF_FISCAL_YEAR").getLabel()+" "+fiscalYear);//'Please fill calendar for %1'
    		}
    	}

    	if(windowType.equalsIgnoreCase("1")) {//ut type using FTE
    		double total_man_days_planned=0;
    		double total_man_days_avail=0;
    		for (int i=1;i<=12;i++){
    			double man_days_planned = dsTotals.getItemNumber(1,"FTE_"+i)/ds_month_hours.getItemNumber(i,"MONTH_HOURS")*hoursPerDay;
    			dsTotals.setItemNumber(1,"FTE_"+i, man_days_planned);//converts to FTE
    			total_man_days_planned += man_days_planned;
    			double man_days_avail = (dsTotals.getItemNumber(2,"FTE_"+i)!=null ? dsTotals.getItemNumber(2,"FTE_"+i) : 0);  //converts nulls to 0s
    			dsTotals.setItemNumber(2,"FTE_"+i, man_days_avail);
    			total_man_days_avail += man_days_avail;
    		}
    		dsTotals.setItemNumber(1,"TOTAL_MAN_DAYS" , total_man_days_planned/12); //avg fte per month - not totals
    		dsTotals.setItemNumber(2,"TOTAL_MAN_DAYS" , total_man_days_avail/12);
    	}
    	else if(windowType.equalsIgnoreCase("2")) {//WY type using men hours
    		double total_man_days_planned=0;
    		double total_man_days_avail=0;
    		for (int i=1;i<=12;i++){
    			total_man_days_planned += (dsTotals.getItemNumber(1,"FTE_"+i)!=null ? dsTotals.getItemNumber(1,"FTE_"+i) : 0); //accumulates sum man hours from plan

    			double man_days_avail = (dsTotals.getItemNumber(2,"FTE_"+i)!=null ? dsTotals.getItemNumber(2,"FTE_"+i) : 0)*ds_month_hours.getItemNumber(i,"MONTH_HOURS");
    			dsTotals.setItemNumber(2,"FTE_"+i, man_days_avail);
    			total_man_days_avail += man_days_avail;
    		}
    		dsTotals.setItemNumber(1,"TOTAL_MAN_DAYS" , total_man_days_planned);
    		dsTotals.setItemNumber(2,"TOTAL_MAN_DAYS" , total_man_days_avail);
    	}
    	else {//default, IN usind man days
    		double total_man_days_avail=0;
    		for (int i=1;i<=ds_month_hours.rowCount();i++){
    			double man_days_avail = (dsTotals.getItemNumber(2,"FTE_"+i)!=null ? dsTotals.getItemNumber(2,"FTE_"+i) : 0)*ds_month_hours.getItemNumber(i,"MONTH_HOURS")/hoursPerDay;
    			dsTotals.setItemNumber(2,"FTE_"+i, man_days_avail);
    			total_man_days_avail += man_days_avail;
    		}
    		dsTotals.setItemNumber(2,"TOTAL_MAN_DAYS" , total_man_days_avail);
    	}
    	if(dl.doesTableExist("OWNER_YEAR_BUDGET")){
    		sql_str = "select sum(a.BUDGET) as BUDGET from OWNER_YEAR_BUDGET a where a.module_id=get_module_id() and a.EFF_FISCAL_YEAR = ?";
    		wc= UserSelection.getSelectionWc(dl,accessRegPlan,"OWNER_YEAR_BUDGET","a");
    		if (wc!=null && wc.length()>0) sql_str+=" AND "+wc;
    		DataStore ds_owner_year_budget = dl.createDataStore(sql_str);
    		ds_owner_year_budget.retrieve(fiscalYear);
    		if(ds_owner_year_budget.rowCount()==1){
    			dsTotals.setItemNumber(2,"total_cost",ds_owner_year_budget.getItemNumber(1,"BUDGET"));
    		}
    	}

    	dsTotals.insertRow(0);
    	dsTotals.setItemString(3,"DESCR","Difference");
    	//double total_man_days=0;
    	for (int i=1;i<=12;i++){
    		double man_days = (dsTotals.getItemNumber(1,"FTE_"+i)!=null ? dsTotals.getItemNumber(1,"FTE_"+i) : 0)
    				- (dsTotals.getItemNumber(2,"FTE_"+i)!=null ? dsTotals.getItemNumber(2,"FTE_"+i) : 0);
    		dsTotals.setItemNumber(3,"FTE_"+i, man_days);
    		//total_man_days += man_days;
    	}
    	//ds_totals.setItemNumber(3,"TOTAL_MAN_DAYS" , total_man_days);
    	dsTotals.setItemNumber(3,"TOTAL_MAN_DAYS" , (dsTotals.getItemNumber(1,"TOTAL_MAN_DAYS")!=null ? dsTotals.getItemNumber(1,"TOTAL_MAN_DAYS") : 0)
    			- (dsTotals.getItemNumber(2,"TOTAL_MAN_DAYS")!=null ? dsTotals.getItemNumber(2,"TOTAL_MAN_DAYS") : 0));
    	if(dsTotals.getItemNumber(1,"DELTA_FTE") !=null && dsTotals.getItemNumber(2,"DELTA_FTE") !=null){
    		dsTotals.setItemNumber(3,"DELTA_FTE",dsTotals.getItemNumber(1,"DELTA_FTE")-dsTotals.getItemNumber(2,"DELTA_FTE"));
    	}
    	if(dsTotals.getItemNumber(1,"TOTAL_COST") !=null && dsTotals.getItemNumber(2,"TOTAL_COST") !=null){
    		dsTotals.setItemNumber(3,"TOTAL_COST",dsTotals.getItemNumber(1,"TOTAL_COST")-dsTotals.getItemNumber(2,"TOTAL_COST"));
    	}

    	return dsTotals;
    }
    
    public double getHoursPerDay()
    {
        return hoursPerDay;
    }
    
    
    /**
     * Set value of instance variable restrictZeros
     * @param restrictZeros if true then we are prevented from allocating resources to months in activity calendar
     * 						that are set to 0
     */
    private void setRestrictZeros(boolean restrictZeros)
    {
    	this.restrictZeros = restrictZeros;
    }
    
    
    /**
     * Get value of instance variable restrictZeros
     * @return true if there is an equipment calendar for the current activity, or the activity calendar
     * 				restricts it explicitly
     */
    private boolean isRestrictZeros()
    {
    	return this.restrictZeros;
    }
    
    
    /**
     * Set value of relative calendar importance.
     * @param importance the level of importance assigned to this calendar
     */
    private void setRelativeCalendarImportance(int importance)
    {
    	this.relativeCalendarImportance = importance;
    }
    
    
    /**
     * Get value of instance variable relativeCalendarImportance.
     * @return the level of importance assigned to this calendar
     */
    private int getRelativeCalendarImportance()
    {
    	return this.relativeCalendarImportance;
    }
    
    
    /**
     * Obtains DATA_SCALE column of SETUP_COLUMN_ID WHERE COLUMN_ID='TOTAL_FTE', which represents the number of
     * digits to be used after the decimal point.  This column should have the same scale as CALC_FTE column. 
     * @return The precision that should be used for numbers in the plan matrix
     * @throws TRDIException
     */
    public int getScale() throws TRDIException
    {
    	if (columnScale == null)
    	{
    		columnScale = getScale("TOTAL_FTE");
    	}
    	
    	return columnScale.intValue();
    }
    
    
    /**
     * Obtains DATA_SCALE column of SETUP_COLUMN_ID WHERE COLUMN_ID='<tt>column</tt>', which represents the number of
     * digits to be used after the decimal point.
     * @param column The COLUMN_ID that we are evaluating
     * @return The precision of <tt>column</tt> in SETUP_COLUMN_ID.
     * @throws TRDIException
     */
    private int getScale(String column) throws TRDIException
    {
    	Integer scale = SetupColumnIdCache.getInstance().getScale(column);
    	if (scale == null)
    	{
    		scale = new Integer(0);
    	}
    	
    	return scale.intValue();
    }
    
    
    /**
     * Checks if the DATA_SCALE column of SETUP_COLUMN_ID matches for the columns TOTAL_FTE and CALC_FTE.
     * @return true if both columns have the same data scale, or CALC_FTE does not exist
     */
    public boolean columnScalesMatch() throws TRDIException
    {
		SetupColumnIdCache sci = SetupColumnIdCache.getInstance();
		
		if (!sci.contains("CALC_FTE"))
		{
			return true;
		}
		
		int totalFteScale = getScale();
		int calcFteScale = getScale("CALC_FTE");
		
		return totalFteScale == calcFteScale;
    }
}
