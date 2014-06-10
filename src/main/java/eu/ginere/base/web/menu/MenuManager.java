package eu.ginere.base.web.menu;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.util.properties.FileProperties.PropertiesChangedLister;
import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightConnector;

public class MenuManager {
	private static PropertiesChangedListerImpl SINGLETON=new PropertiesChangedListerImpl();
	
	private static final Hashtable <String,List<MenuItem>> cache=new Hashtable <String,List<MenuItem>>();

	private static final Hashtable <Language,Map<String,List<MenuItem>>> i18nCache=new Hashtable <Language,Map<String,List<MenuItem>>>();

	private static final String MENU_SECTION = "MENU_LABELS_SECTION";


	private static List<MenuItem> getI18nCacheMenuItem(String id,Language lang){
		Map <String,List<MenuItem>> langCache;
		if (i18nCache.containsKey(lang)){
			langCache=i18nCache.get(lang);
		} else {
			langCache=new Hashtable <String,List<MenuItem>>();
			i18nCache.put(lang,langCache);
		}

		if (langCache.containsKey(id)){
			return langCache.get(id);
		} else {
			List<MenuItem> orig=getCacheMenuItem(id);
			List<MenuItem> translated=translateMenuItemList(orig,lang);

			langCache.put(id,translated);

			return translated;
		}
	}
	
	public static void clearI18NCache() {
		i18nCache.clear();		
	}

	private static List<MenuItem> getCacheMenuItem(String id){
		if (cache.containsKey(id)){
			return cache.get(id);
		} else {
			
			String subMenuArray[]=GlobalFileProperties.getPropertyList(MenuManager.class,"MenuItemList_"+id);
			List <MenuItem>menu=new ArrayList<MenuItem>(subMenuArray.length);
			
			for(String element:subMenuArray){
				String label=GlobalFileProperties.getStringValue(MenuManager.class, element+"_Label","Item Not Found");
				String right=GlobalFileProperties.getStringValue(MenuManager.class, element+"_Right",null);
				String url=GlobalFileProperties.getStringValue(MenuManager.class, element+"_URL",null);
				String cls=GlobalFileProperties.getStringValue(MenuManager.class, element+"_CLS",null);
				boolean window=GlobalFileProperties.getBooleanValue(MenuManager.class, element+"_WINDOW",false);
				boolean isSeparator=GlobalFileProperties.getBooleanValue(MenuManager.class, element+"_SEP",false);
				
				List<MenuItem> itemList=getItemList(element);
				MenuItem menuItem=new MenuItem(element,label,right,url,cls,isSeparator,window,itemList);
				menu.add(menuItem);
			}
		
		 cache.put(id,menu);
		 return menu;
		}
	}
	
	private static List<MenuItem> getItemList(String id){
		String itemsArray[]=GlobalFileProperties.getPropertyList(MenuManager.class,id+"_ItemList");
		List <MenuItem>menu=new ArrayList<MenuItem>(itemsArray.length);
		for(String element:itemsArray){
			String label=GlobalFileProperties.getStringValue(MenuManager.class, element+"_Label","Item Not Found");
			String right=GlobalFileProperties.getStringValue(MenuManager.class, element+"_Right",null);
			String url=GlobalFileProperties.getStringValue(MenuManager.class, element+"_URL",null);
			String cls=GlobalFileProperties.getStringValue(MenuManager.class, element+"_CLS",null);
			boolean isSeparator=GlobalFileProperties.getBooleanValue(MenuManager.class, element+"_SEP",false);
			boolean window=GlobalFileProperties.getBooleanValue(MenuManager.class, element+"_WINDOW",false);
			List<MenuItem> itemList=getItemList(element);
			
			MenuItem menuItem=new MenuItem(element,label,right,url,cls,isSeparator,window,itemList);
			menu.add(menuItem);
		}
		
		return menu;
	}
		
	
	public static List<MenuItem> getMenuItem(String id,String userId){
		List<MenuItem> menu = getCacheMenuItem(id);

		return checkRights(menu, userId);	 
	}

	public static List<MenuItem> getI18nMenuItem(String id, String userId,
												 Language langId) {
		List<MenuItem> menu = getI18nCacheMenuItem(id,langId);

		return checkRights(menu, userId);
	}
	
	private static List<MenuItem> checkRights(List<MenuItem> menu,String userId){
		// compruebo si el usuario tiene los derechos

		List<MenuItem> menuRight = new ArrayList<MenuItem>(menu.size());
		for (MenuItem element : menu) {
			if (element.hasChilds()){
				List<MenuItem> childs=checkRights(element.getItemList(), userId);
				if (childs.size()>0){
					menuRight.add(new MenuItem(element,childs));
				} else if (RightConnector.hasRight(userId, element.getRight())) {
					menuRight.add(new MenuItem(element));
				}
					
			} else if (RightConnector.hasRight(userId, element.getRight())) {
				menuRight.add(element);
			}
		}
		return menuRight;	 
	}
	

	public static List<MenuItem> translateMenuItemList(List<MenuItem> list,Language langId){
		List<MenuItem> ret=new ArrayList<MenuItem>(list.size());
		
		for (MenuItem menuItem:list){
			MenuItem translatedMenu=translateMenuItem(menuItem,langId);

			ret.add(translatedMenu);
		}

		return ret;
	}
	
	public static MenuItem translateMenuItem(MenuItem menuItem,Language langId){
		List<MenuItem> childs=menuItem.getItemList();

		List<MenuItem> translatedChilds=translateMenuItemList(childs,langId);

		String trasnlatedlabel=I18NConnector.getLabel(langId, MENU_SECTION, menuItem.getLabel());

		return new MenuItem(menuItem.getId(),trasnlatedlabel,menuItem.getRight(),menuItem.getUrl(),menuItem.getCls(),menuItem.isSeparator(),menuItem.isWindow(),translatedChilds);
		
	}

	static private class PropertiesChangedListerImpl implements PropertiesChangedLister{
		
			
		private PropertiesChangedListerImpl(){
			GlobalFileProperties.subscriveToPropertiesChanged(this);
				
		}
		/**
		 * @param lastModified fecha de la modificacion precedente.
		 * Si no ha habido, es la primera vez que se carga el fichero esta fecha es 0.
		 * Todos los listeners son llamados desde el mismo hilo si alguno se bloquea o 
		 * tarda demasiado la aplicacion quedara bloqueada. Por favor si tienes que hacer 
		 * muchas cosas crea un Hilo
		 */
		public void propertiesChanged(long lastModified){
			cache.clear();
		}
	}
}
