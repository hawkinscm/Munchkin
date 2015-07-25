
package model.card;

import gui.GUI;

import javax.swing.ImageIcon;

/**
 * Class that represents a Munchkin playing card.
 */
public class Card {
	
	// Monster Card IDs
	public static int M_3872_ORCS 								= 101;
	public static int M_AMAZON 									= 102;
	public static int M_BIGFOOT 								= 103;
	public static int M_BULLROG 								= 104;
	public static int M_CRABS 									= 105;
	public static int M_DROOLING_SLIME 							= 106;
	public static int M_FACE_SUCKER 							= 107;
	public static int M_FLOATING_NOSE 							= 108;
	public static int M_FLYING_FROGS 							= 109;
	public static int M_GAZEBO 									= 110;
	public static int M_GELATINOUS_OCTAHEDRON 					= 111;
	public static int M_GHOULFIENDS 							= 112;
	public static int M_HARPIES 								= 113;
	public static int M_HIPPOGRIFF 								= 114;
	public static int M_INSURANCE_SALESMAN 						= 115;
	public static int M_KING_TUT 								= 116;
	public static int M_LAME_GOBLIN 							= 117;
	public static int M_LARGE_ANGRY_CHICKEN 					= 118;
	public static int M_LAWYER 									= 119;
	public static int M_LEPERCHAUN 								= 120;
	public static int M_MAUL_RAT 								= 121;
	public static int M_MR_BONES 								= 122;
	public static int M_NET_TROLL 								= 123;
	public static int M_PIT_BULL 								= 124;
	public static int M_PLATYCORE 								= 125;
	public static int M_PLUTONIUM_DRAGON 						= 126;
	public static int M_POTTED_PLANT 							= 127;
	public static int M_SHRIEKING_GEEK 							= 128;
	public static int M_SNAILS_OF_SPEED 						= 129;
	public static int M_SQUIDZILLA 								= 130;
	public static int M_STONE_GOLEM 							= 131;
	public static int M_THE_NOTHING 							= 132;
	public static int M_TONGUE_DEMON 							= 133;
	public static int M_UNDEAD_HORSE 							= 134;
	public static int M_UNSPEAKABLY_AWFUL_INDESCRIBABLE_HORROR 	= 135;
	public static int M_WANNABE_VAMPIRE 						= 136;
	public static int M_WIGHT_BROTHERS 							= 137;
	
	// Race Card IDs
	public static int R_ELF_1									= 201;
	public static int R_ELF_2									= 202;
	public static int R_ELF_3									= 203;
	public static int R_DWARF_1									= 204;
	public static int R_DWARF_2									= 205;
	public static int R_DWARF_3									= 206;
	public static int R_HALFLING_1								= 207;
	public static int R_HALFLING_2								= 208;
	public static int R_HALFLING_3								= 209;
	
	// Class Card IDs
	public static int CL_CLERIC_1								= 301;
	public static int CL_CLERIC_2								= 302;
	public static int CL_CLERIC_3								= 303;
	public static int CL_THIEF_1								= 304;
	public static int CL_THIEF_2								= 305;
	public static int CL_THIEF_3								= 306;
	public static int CL_WARRIOR_1								= 307;
	public static int CL_WARRIOR_2								= 308;
	public static int CL_WARRIOR_3								= 309;
	public static int CL_WIZARD_1								= 310;
	public static int CL_WIZARD_2								= 311;
	public static int CL_WIZARD_3								= 312;	
	
	// Curse Card IDs
	public static int CU_CHANGE_CLASS							= 401;
	public static int CU_CHANGE_RACE							= 402;
	public static int CU_CHANGE_SEX								= 403;
	public static int CU_CHICKEN_ON_YOUR_HEAD					= 404;
	public static int CU_DUCK_OF_DOOM							= 405;
	public static int CU_INCOME_TAX								= 406;
	public static int CU_LOSE_1_BIG_ITEM						= 407;
	public static int CU_LOSE_1_LEVEL_1							= 408;
	public static int CU_LOSE_1_LEVEL_2							= 409;
	public static int CU_LOSE_1_SMALL_ITEM_1					= 410;
	public static int CU_LOSE_1_SMALL_ITEM_2					= 411;
	public static int CU_LOSE_THE_ARMOR_YOU_ARE_WEARING			= 412;
	public static int CU_LOSE_THE_FOOTGEAR_YOU_ARE_WEARING		= 413;
	public static int CU_LOSE_THE_HEADGEAR_YOU_ARE_WEARING		= 414;
	public static int CU_LOSE_TWO_CARDS							= 415;
	public static int CU_LOSE_YOUR_CLASS						= 416;
	public static int CU_LOSE_YOUR_RACE							= 417;
	public static int CU_MALIGN_MIRROR							= 418;
	public static int CU_TRULY_OBNOXIOUS_CURSE					= 419;
	
	// Monster Enhancer Card IDs
	public static int ME_ANCIENT								= 501;
	public static int ME_BABY									= 502;
	public static int ME_ENRAGED								= 503;
	public static int ME_HUMONGOUS								= 504;
	public static int ME_INTELLIGENT							= 505;
	
	// Other Door Card IDs
	public static int OD_CHEAT									= 601;
	public static int OD_DIVINE_INTERVENTION					= 602;
	public static int OD_HALF_BREED_1							= 603;
	public static int OD_HALF_BREED_2							= 604;
	public static int OD_HELP_ME_OUT_HERE						= 605;
	public static int OD_ILLUSION								= 606;
	public static int OD_MATE									= 607;
	public static int OD_OUT_TO_LUNCH							= 608;
	public static int OD_SUPER_MUNCHKIN_1						= 609;
	public static int OD_SUPER_MUNCHKIN_2						= 610;
	public static int OD_WANDERING_MONSTER_1					= 611;
	public static int OD_WANDERING_MONSTER_2					= 612;
	
	// Equipment Card IDs
	public static int E_BOOTS_OF_BUTT_KICKING					= 701;
	public static int E_BOOTS_OF_RUNNING_REALLY_FAST			= 702;
	public static int E_BOW_WITH_RIBBONS						= 703;
	public static int E_BROAD_SWORD								= 704;
	public static int E_BUCKLER_OF_SWASHING						= 705;
	public static int E_CHEESE_GRATER_OF_PEACE					= 706;
	public static int E_CLOAK_OF_OBSCURITY						= 707;
	public static int E_DAGGER_OF_TREACHERY						= 708;
	public static int E_ELEVEN_FOOT_POLE						= 709;
	public static int E_FLAMETHROWER							= 710;
	public static int E_FLAMING_ARMOR							= 711;
	public static int E_GENTLEMENS_CLUB							= 712;
	public static int E_HAMMER_OF_KNEECAPPING					= 713;
	public static int E_HELM_OF_COURAGE							= 714;
	public static int E_HORNED_HELMET							= 715;
	public static int E_HUGE_ROCK								= 716;
	public static int E_KNEEPADS_OF_ALLURE						= 717;
	public static int E_LEATHER_ARMOR							= 718;
	public static int E_LIMBURGER_AND_ANCHOVY_SANDWICH			= 719;
	public static int E_MACE_OF_SHARPNESS						= 720;
	public static int E_MITHRIL_ARMOR							= 721;
	public static int E_PANTYHOSE_OF_GIANT_STRENGTH				= 722;
	public static int E_POINTY_HAT_OF_POWER						= 723;
	public static int E_RAD_BANDANNA							= 724;
	public static int E_RAPIER_OF_UNFAIRNESS					= 725;
	public static int E_RAT_ON_A_STICK							= 726;
	public static int E_REALLY_IMPRESSIVE_TITLE					= 727;
	public static int E_SANDALS_OF_PROTECTION					= 728;
	public static int E_SHIELD_OF_UBIQUITY						= 729;
	public static int E_SHORT_WIDE_ARMOR						= 730;
	public static int E_SINGING_AND_DANCING_SWORD				= 731;
	public static int E_SLIMY_ARMOR								= 732;
	public static int E_SNEAKY_BACKSWORD						= 733;
	public static int E_SPIKY_KNEES								= 734;
	public static int E_STAFF_OF_NAPALM							= 735;
	public static int E_STEPLADDER								= 736;
	public static int E_SWISS_ARMY_POLEARM						= 737;
	public static int E_TUBA_OF_CHARM							= 738;
	
	// Item Card IDs
	public static int I_COTION_OF_PONFUSION						= 801;
	public static int I_DOPPLEGANGER							= 802;
	public static int I_ELECTRIC_RADIOACTIVE_ACID_POTION		= 803;
	public static int I_FLAMING_POISON_POTION					= 804;
	public static int I_FLASK_OF_GLUE							= 805;
	public static int I_FREEZING_EXPLOSIVE_POTION				= 806;
	public static int I_FRIENDSHIP_POTION						= 807;
	public static int I_INSTANT_WALL							= 808;
	public static int I_INVISIBILITY_POTION						= 809;
	public static int I_LOADED_DIE								= 810;
	public static int I_MAGIC_LAMP_1							= 811;
	public static int I_MAGIC_LAMP_2							= 812;
	public static int I_MAGIC_MISSILE							= 813;
	public static int I_NASTY_TASTING_SPORTS_DRINK				= 814;
	public static int I_POLLYMORPH_POTION						= 815;
	public static int I_POTION_OF_HALITOSIS						= 816;
	public static int I_POTION_OF_IDIOTIC_BRAVERY				= 817;
	public static int I_PRETTY_BALLOONS							= 818;
	public static int I_SLEEP_POTION							= 819;
	public static int I_TRANSFERRAL_POTION						= 820;
	public static int I_WAND_OF_DOWSING							= 821;
	public static int I_WISHING_RING_1							= 822;
	public static int I_WISHING_RING_2							= 823;
	public static int I_YUPPIE_WATER							= 824;
	
	// Go Up A Level Card IDs
	public static int GUL_1000_GOLD_PIECES						= 901;
	public static int GUL_BOIL_AN_ANTHILL						= 902;
	public static int GUL_BRIBE_GM_WITH_FOOD					= 903;
	public static int GUL_CONVENIENT_ADDITION_ERROR				= 904;
	public static int GUL_INVOKE_OBSCURE_RULES					= 905;
	public static int GUL_KILL_THE_HIRELING						= 906;
	public static int GUL_MOW_THE_BATTLEFIELD					= 907;
	public static int GUL_POTION_OF_GENERAL_STUDLINESS			= 908;
	public static int GUL_WHINE_AT_THE_GM						= 909;
	
	// Other Treasure Card IDs
	public static int OT_STEAL_A_LEVEL							= 1001;
	public static int OT_HIRELING								= 1002;
	public static int OT_HOARD									= 1003;
	
	/**
	 * The unique id of the card;
	 */
	protected int id;
	/**
	 * The name of the card;
	 */
	protected String name;
	/**
	 * The picture of the card.
	 */
	protected ImageIcon picture = null;
	
	/**
	 * Defines the parameters that subclasses must have.
	 * @param i unique id of the card
	 * @param n name of the card
	 */
	protected Card(int i, String n) {
		id = i;
		name = n;
	}
	
	@Override
	/**
	 * Override to return the display name of the card (removes any end index numbers that makes the card name unique).
	 * @return the display name of the card
	 */
	public String toString() {
		return name;
	}
	
	@Override
	/**
	 * Returns whether or not the given object is the same as this object.
	 * @param o the object being compared
	 * @return true if the object is a Card and has the same name as this Card; false otherwise
	 */
	public boolean equals(Object o) {
		if (o instanceof Card)
			return (id == ((Card)o).getID());
		
		return false;
	}
	
	/**
	 * Returns the ID of this card.
	 * @return the ID of this card
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the name of this card.
	 * @return the name of this card
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the picture for this Card.
	 * @return the picture for this Card
	 */
	public ImageIcon getPicture() {
		try {
			return new ImageIcon(GUI.class.getResource("images/" + id + ".jpg"));
		}
		catch (Exception ex) {
			return new ImageIcon(GUI.class.getResource("images/blank.jpg"));
		}
	}	
}
