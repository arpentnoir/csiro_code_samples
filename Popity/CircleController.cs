using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;

public class CircleController : MonoBehaviour {

	public AudioClip popfizzle; 
	private AudioSource audio;
	private List<GameObject> touchingList; // the list of circles touching this one
	private List<GameObject> list; // the list of same colour circles, set in LevelManager


	private SpriteRenderer spriteRenderer; // sprite renderer of circle, used to set colour
	private Color color; // used to set color of particles, not necessarily the color of the circle
	private bool shrinking; 

	public float force;
	public float radius;

	public float pitchRangeBottom = 0.2f; // lowest pitch that a bubble pop sound can produce
	public float pitchRangeTop = 2.0f; // highest pitch a bubble pop sound can produce
	public float shrinkScaleFactor = 0.9f;  // determines how fast a popped bubble shrinks
	public float popDelay = 0.1f; // determines the gap between bubble pops from a single touch event
	public float baseCircleRadius = 2.56f; // the radius of a bubble before it is scaled randomly
	public float popPropagationBuffer = 0.7f; // determines how far apart bubbles need to be to be considered touching. 
	public float popVolume = 0.7f; // volume of audio source for pop sound
	public float destroyBubbleDelay = 2.0f; // how long before a bubble game object is actually removed after being 'popped' 

	public void Setup(Sprite s, Color c, List<GameObject> l){
		GetComponentInChildren<SpriteRenderer> ().sprite = s;
		SetColor(c);
		list = l;
		audio = gameObject.AddComponent<AudioSource> ();
		audio.pitch = Random.Range(pitchRangeBottom, pitchRangeTop);
	}


	void Start () {
		spriteRenderer = gameObject.GetComponent<SpriteRenderer> ();
		touchingList = new List<GameObject>{ gameObject }; // list of circles touching this one, initialised with just this game object

	}

	/*
	 * on each iteration:
	 * 	- scale circle if it's supposed to be shrinking
	 * 	- handle user input and;
	 * 		- get the list of touching bubbles
	 * 		- shedule pop for each bubble in the touching list
	 */ 
	void Update () {
		if (shrinking) {
			transform.localScale = new Vector3 (transform.localScale.x * shrinkScaleFactor, transform.localScale.y * shrinkScaleFactor, transform.localScale.z * shrinkScaleFactor);

		}
		
		if (Input.GetMouseButtonDown(0)) {
			Vector3 wp = Camera.main.ScreenToWorldPoint (Input.mousePosition);
			Vector2 touchPos = new Vector2 (wp.x, wp.y);

			if (transform.GetComponentInChildren<CircleCollider2D> ().OverlapPoint (touchPos)) {
				
				touchingList = GetTouchingList (list, touchingList);


				if (touchingList.Count > 1) {
					GameManager.manager.UpdatePopAchievements (touchingList.Count);
					GameManager.manager.IncrementScore (touchingList.Count * (touchingList.Count - 1));
					for (int i = 0; i < touchingList.Count; i++) {
						touchingList [i].GetComponentInChildren<CircleController> ().ScheduleDestroy (i * popDelay);
					}

				}

			}
		}
	
	}

	/*
	 * Check recursively for bubbles touching this one, or bubbles touching bubbles touching this one, etc... 
	 *
	 */

	private List<GameObject> GetTouchingList(List<GameObject> list, List<GameObject> touchingList){
		List<GameObject> tmpList = new List<GameObject>();

		foreach (GameObject circle in touchingList) {
			foreach (GameObject candidate in list) {
				if(circle.GetInstanceID() != candidate.GetInstanceID() && Vector3.Distance(circle.transform.position, candidate.transform.position) 
					< ((baseCircleRadius * circle.transform.localScale.x) + (baseCircleRadius * candidate.transform.localScale.x) + popPropagationBuffer) ){
					
					tmpList.Add (candidate);
				}
			}
		}
		if (tmpList.Count > 0) {
			foreach(GameObject touchingCircle in tmpList){
				if (!touchingList.Contains (touchingCircle)) {
					touchingList.Add (touchingCircle);
				}
				list.Remove (touchingCircle);	
			}
			return GetTouchingList (list, touchingList);
		} else {
			return touchingList;
		}
	}

	public void SetColor(Color c){
		gameObject.GetComponentInChildren<ParticleSystem> ().startColor = c;
		gameObject.GetComponentInChildren<SpriteRenderer> ().color = c;
	}

	public Color GetColor(){
		return spriteRenderer.color;
	}

	public void SetList(List<GameObject> l){
		list = l;
	}

	/*
	 * handle the process of popping blubbles by:
	 * 	- adding a repulsive force to surrounding bubbles
	 *  - playing a pop sound
	 *  - set the bubbles shrinking property to true
	 *  - eject particles
	 *  - schedule actual destroy of the game object
	 */

	void destroy(){
		Collider2D[] colliders = Physics2D.OverlapCircleAll (transform.position, radius);
		foreach(Collider2D c in colliders){
			if (c.attachedRigidbody == null) // i.e. we only want to blow up other circles
				continue;

			AddExplosionForce (c.attachedRigidbody, force, transform.position, radius);
		}
		if(GameManager.manager.soundOn){
			audio.PlayOneShot (popfizzle, popVolume);
			print (audio.pitch);
		}
		shrinking = true;
		gameObject.GetComponentInChildren<ParticleSystem> ().Play ();
		list.Remove (gameObject);
		Invoke("DestroyThis", destroyBubbleDelay);
	}

	// called by update with a time based on distance from touched bubble.
	void ScheduleDestroy(float time){
		Invoke ("destroy", time);
	}

	// remove the game object, called by destroy()
	void DestroyThis(){
		Destroy (gameObject);
	}

	public static void AddExplosionForce (Rigidbody2D body, float expForce, Vector3 expPosition, float expRadius)
	{
		var dir = (body.transform.position - expPosition);
		float calc = 1 - (dir.magnitude / expRadius);
		if (calc <= 0) {
			calc = 0;		
		}

		body.AddForce (dir.normalized * expForce * calc);
	}

}






