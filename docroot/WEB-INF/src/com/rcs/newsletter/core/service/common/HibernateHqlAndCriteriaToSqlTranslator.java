package com.rcs.newsletter.core.service.common;

import java.lang.reflect.Field;
import java.util.Collections;
 
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
 
public class HibernateHqlAndCriteriaToSqlTranslator {
  private SessionFactory sessionFactory;
 
  public void setSessionFactory(SessionFactory sessionFactory){
    this.sessionFactory = sessionFactory;
  }
 
  public static String toSql(Criteria criteria){
    try{
      CriteriaImpl c = (CriteriaImpl) criteria;
      SessionImpl s = (SessionImpl)c.getSession();
      SessionFactoryImplementor factory = (SessionFactoryImplementor)s.getSessionFactory();
      String[] implementors = factory.getImplementors( c.getEntityOrClassName() );
    //CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable)factory.getEntityPersister(implementors[0]), factory, c, implementors[0], s.getEnabledFilters());
      CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable)factory.getEntityPersister(implementors[0]), factory, c, implementors[0], s.getLoadQueryInfluencers());
      Field f = OuterJoinLoader.class.getDeclaredField("sql");
      f.setAccessible(true);
      return (String) f.get(loader);
    }
    catch(Exception e){
      throw new RuntimeException(e); 
    }
  }
 
  public String toSql(String hqlQueryText){
    if (hqlQueryText!=null && hqlQueryText.trim().length()>0){
      final QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
      final SessionFactoryImplementor factory =  (SessionFactoryImplementor) sessionFactory;
      final QueryTranslator translator = translatorFactory.
        createQueryTranslator(
          hqlQueryText, 
          hqlQueryText, 
          Collections.EMPTY_MAP, factory
        );
      translator.compile(Collections.EMPTY_MAP, false);
      return translator.getSQLString(); 
    }
    return null;
  }
}
