package com.sad.architecture.api.componentization;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/4/23 0023.
 */

public interface IVisitor<V extends IVisitor<V>> {

    Api api();

    ILauncher launchNode();

    V addComponentConstructor(String name,IComponentInstanceConstructor componentInstanceConstructor);

    V addComponentConstructors(Map<String,IComponentInstanceConstructor> constructorMap);

    interface Api{

        IComponentInstanceFactory factory();

        <IR extends IRequester.Api> IR requesterApi();

        Map<String,IComponentInstanceConstructor> componentConstructors();

        IComponentInstanceConstructor componentConstructor(String name);

        default <IC> List<IC> require(String name) throws Exception{
            IComponentInstanceFactory factory = factory();
            if (factory!=null /*&& requesterApi()!=null*/){
                IComponentInstanceConstructor constructor=componentConstructor(name);
                return factory.require(name,constructor);
            }
            return null;
        }
    }


    /*interface ICaller<C extends ICaller<C>> extends IVisitor<C>{

        C constructorClass(Class... constructorClass);

        C constructorParameters(Object... constructorParameters);

        @Override
        ICallerApiGetter api();

        interface ICallerApiGetter extends Api{

        }

    }

    interface IPoster<P extends IPoster<P>> extends IVisitor<P>{

        @Override
        IPosterApiGetter api();

        interface IPosterApiGetter extends Api{

        }

    }

    interface IExposer<E extends IExposer<E>> extends IVisitor<E>{

        @Override
        IExposerApiGetter api();

        <E extends IComponent> E serviceInterface() throws Exception;

        interface IExposerApiGetter extends Api{

            String name();
        }

        interface IPostedExposer<PE extends IPostedExposer<PE>> extends IExposer<PE>{

            @Override
            default <E extends IComponent> E serviceInterface() throws Exception{
                if (api()!=null){
                    IComponentInstanceFactory factory = api().factory();
                    if (factory!=null){
                        return factory.require(api().name(),null,"");
                    }
                }
                return null;
            }

            @Override
            IPostedExposerApiGetter api();

            interface IPostedExposerApiGetter extends IExposerApiGetter,IPoster.IPosterApiGetter {

            }
        }
        interface ICalledExposer<CE extends ICalledExposer<CE>> extends IExposer<CE>{


            CE constructorClass(Class... constructorClass);

            CE constructorParameters(Object... constructorParameters);

            @Override
            ICalledExposerApiGetter api();

            @Override
            default <IC extends IComponent> IC serviceInterface() throws Exception{
                if (api()!=null){
                    IComponentInstanceFactory factory = api().factory();
                    if (factory!=null){
                        return factory.require(api().name(), api().constructorClass(), api().constructorParameters());
                    }
                }
                return null;
            }

            interface ICalledExposerApiGetter extends IExposerApiGetter,ICaller.ICallerApiGetter {

                Class[] constructorClass();

                Object[] constructorParameters();
            }
        }
    }*/




    /*final static int CALLER=0;
    final static int POSTER=1;
    @IntDef({CALLER,POSTER})
    @Retention(RetentionPolicy.SOURCE)
    @Target({PARAMETER,METHOD,FIELD, LOCAL_VARIABLE})
    @interface RequestModeIntDef {}*/

}
