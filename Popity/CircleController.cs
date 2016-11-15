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

	public void Setup(Sprite s, Color c, List<GameObject> l){
		GetComponentInChildren<SpriteRenderer> ().sprite = s;
		SetColor(c);
		list = l;
		audio = gameObject.AddComponent<AudioSource> ();
		audio.pitch = Random.Range(0.2f, 2.0f); 
	}


	void Start () {
		spriteRenderer = gameObject.GetComponent<SpriteRenderer> ();
		touchingList = new List<GameObject>{ gameObject }; // initial list of circles touching this one, initialised with just this game object

	}

	void Update () {
		if (shrinking) {
			transform.localScale = new Vector3 (transform.localScale.x * 0.9f, transform.localScale.y * 0.9f, transform.localScale.z * 0.9f);

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
						touchingList [i].GetComponentInChildren<CircleController> ().ScheduleDestroy (i * 0.1f);
					}

				}

			}
		}
	
	}


	private List<GameObject> GetTouchingList(List<GameObject> list, List<GameObject> touchingList){
		List<GameObject> tmpList = new List<GameObject>();

		foreach (GameObject circle in touchingList) {
			foreach (GameObject candidate in list) {
				if(circle.GetInstanceID() != candidate.GetInstanceID() && Vector3.Distance(circle.transform.position, candidate.transform.position) < ((2.56f * circle.transform.localScale.x) + (2.56f * candidate.transform.localScale.x) + 0.07f) ){
					
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

	void destroy(){
		Collider2D[] colliders = Physics2D.OverlapCircleAll (transform.position, radius);
		foreach(Collider2D c in colliders){
			if (c.attachedRigidbody == null)
				continue;

			AddExplosionForce (c.attachedRigidbody, force, transform.position, radius);
		}
		if(GameManager.manager.soundOn){
			audio.PlayOneShot (popfizzle, 0.7f);
			print (audio.pitch);
		}
		shrinking = true;
		gameObject.GetComponentInChildren<ParticleSystem> ().Play ();
		list.Remove (gameObject);
		Invoke("DestroyThis", 2.0f);
	}

	void ScheduleDestroy(float time){
		Invoke ("destroy", time);
	}


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






