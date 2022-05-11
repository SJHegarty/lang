/*

$[private statefull type] Blah ~ [ParentZero, ParentOne] {
	$[private type] Inner{
	
	}
}


	<: class-name ~ Regular {
		<: segment ~ Regular :: ['A'...'Z']*[1..]['a'...'z'];
		<: @rv ~ Regular :: *[1..]@segment;
	}
	<: <key-word, key-words> ~ <Regular, Regular> {
		<: prefix ~ Char :: $;
		<: base-word ~ Regular :: *[1..]['a'...'z'];
		<: @key-word ~ Regular :: @prefix; @base-word;
		<: @key-words ~ Regular :: +(@key-word;, @prefix; '[' *[1..]@base-word; ']');
	}
	<: class ~ Irregular {
		<: contstraints ~...
		<: header ~ Regular :: ?@key-words; @class-name *@constraints;; 
		
	}
*/
class Machine{}
