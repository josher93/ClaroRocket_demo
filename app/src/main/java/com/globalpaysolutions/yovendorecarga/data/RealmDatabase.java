package com.globalpaysolutions.yovendorecarga.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.globalpaysolutions.yovendorecarga.model.Operator;
import com.globalpaysolutions.yovendorecarga.model.OperatorsBalance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Josu� Ch�vez on 09/12/2016.
 */
public final class RealmDatabase
{

    private static Context _context;
    private static Realm RealmObject;
    private static RealmDatabase singletonRealm;

    private RealmDatabase(Context context)
    {
        _context = context;
        Realm.init(context);
        RealmObject = Realm.getDefaultInstance();
    }

    public static synchronized RealmDatabase getInstance(Context pContext)
    {
        if (singletonRealm == null)
        {
            singletonRealm = new RealmDatabase(pContext);
        }
        return singletonRealm;
    }

    public void InsertOperatorBalance(JSONObject pOperatorBalance)
    {
        String operatorName = "";
        int operatorId = 0;
        String balance = "";

        try
        {
            operatorName = pOperatorBalance.has("mobileOperator") ? pOperatorBalance.getString("mobileOperator") : "";
            operatorId = pOperatorBalance.has("operatorId") ? pOperatorBalance.getInt("operatorId") : 0;
            balance = pOperatorBalance.has("balance") ? pOperatorBalance.getString("balance") : "";
        }
        catch (JSONException jEx)
        {
            jEx.printStackTrace();
        }

        RealmObject.beginTransaction();
        OperatorsBalance operatorBalance = new OperatorsBalance();
        operatorBalance.setMobileOperator(operatorName);
        operatorBalance.setOperatorId(operatorId);
        operatorBalance.setBalance(balance);
        RealmObject.copyToRealmOrUpdate(operatorBalance);
        RealmObject.commitTransaction();

    }

    public void InsertMultipleOperatorsBalance(final List<OperatorsBalance> pOperatorsList)
    {
        try
        {
            //Si la lista de operadoras viene vacía, entonces borra los actuales
            /*if(pOperatorsList.size() == 0)
            {
                final RealmResults<OperatorsBalance> results = RealmObject.where(OperatorsBalance.class).findAll();

                RealmObject.executeTransaction(new Realm.Transaction()
                {
                    @Override
                    public void execute(Realm realm)
                    {
                        results.deleteAllFromRealm();
                    }
                });
            }
            else
            {*/

            final RealmResults<OperatorsBalance> results = RealmObject.where(OperatorsBalance.class).findAll();

            RealmObject.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    results.deleteAllFromRealm();
                }
            });

            RealmObject.beginTransaction();
            for (OperatorsBalance item : pOperatorsList)
            {
                OperatorsBalance operatorBalance = new OperatorsBalance();
                operatorBalance.setMobileOperator(item.getMobileOperator());
                operatorBalance.setOperatorId(item.getOperatorId());
                operatorBalance.setBalance(item.getBalance());
                RealmObject.copyToRealmOrUpdate(operatorBalance);

            }
            RealmObject.commitTransaction();
            //}
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public List<OperatorsBalance> GetOperatorsBalance()
    {
        RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class);
        RealmResults<OperatorsBalance> result = query.findAll();

        return result;
    }

    public OperatorsBalance SingleOperatorBalance(int pId)
    {
        OperatorsBalance found = new OperatorsBalance();
        try
        {
            RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class).equalTo("operatorId", pId);
            found = query.findFirst();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return found;
    }

    public String OperatorBalanceByName(String pName)
    {
        OperatorsBalance found;
        String balance = "";

        try
        {
            if (pName.isEmpty())
            {
                RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class);
                RealmResults<OperatorsBalance> result = query.findAll();

                //Obtiene el de mayor monto
                /*double max = Integer.MIN_VALUE;
                int index = Integer.MIN_VALUE;

                for (int i = 0; i < result.size(); i++)
                {
                    if (Double.parseDouble(result.get(i).getBalance()) > max)
                    {
                        max = Double.parseDouble(result.get(i).getBalance());
                        index = i;
                    }
                }*/

                found = result.get(0);
                balance = found.getBalance();
            }
            else
            {
                RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class).equalTo("mobileOperator", pName);
                found = query.findFirst();
                balance = found.getBalance();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return balance;
    }

    public String OperatorName(String pName)
    {
        OperatorsBalance found;
        String name = "";

        try
        {
            if (pName.isEmpty())
            {
                RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class);
                RealmResults<OperatorsBalance> result = query.findAll();

                found = result.get(0);
                name = found.getMobileOperator();
            }
            else
            {
                RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class).equalTo("mobileOperator", pName);
                found = query.findFirst();
                name = found.getMobileOperator();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return name;
    }

    public void DeleteInvalidOperators(final List<Operator> pOperators)
    {
        try
        {
            final RealmResults<OperatorsBalance> results = RealmObject.where(OperatorsBalance.class).findAll();

            RealmObject.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    for (OperatorsBalance saved : results)
                    {
                        //Nuevo
                        for (Operator item : pOperators)
                        {
                            OperatorsBalance found;
                            RealmQuery<OperatorsBalance> query = RealmObject.where(OperatorsBalance.class).equalTo("operatorId", saved.getOperatorId());
                            found = query.findFirst();

                            if (found.getOperatorId() != item.getID())
                            {
                                found.deleteFromRealm();
                            }
                        }
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Log.e("REALM", ex.getMessage());
            ex.printStackTrace();
        }
    }
}

